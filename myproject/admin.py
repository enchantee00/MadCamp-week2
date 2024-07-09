import streamlit as st
import requests
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import seaborn as sns
from http.cookies import SimpleCookie
from statsmodels.tsa.arima.model import ARIMA
from statsmodels.tsa.statespace.sarimax import SARIMAX


# HTTP Server 시작
import http.server
import socketserver
import threading

PORT = 8000

Handler = http.server.SimpleHTTPRequestHandler
Handler.extensions_map.update({
    '.ttf': 'assets/Blobtastics.ttf',
})

def serve_fonts():
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        httpd.serve_forever()

thread = threading.Thread(target=serve_fonts)
thread.daemon = True
thread.start()


base_url = "http://172.10.7.97:80/api/"

# 쿠키 설정 및 읽기 함수
def set_cookie(key, value):
    max_age = 30 * 24 * 60 * 60  # 30 days
    expires = max_age
    cookie = SimpleCookie()
    cookie[key] = value
    cookie[key]['path'] = '/'
    cookie[key]['max-age'] = max_age
    cookie[key]['expires'] = expires
    st.experimental_set_query_params(__cookie=str(cookie.output(header='', sep='')).strip())

def get_cookie(key):
    query_params = st.experimental_get_query_params()
    cookie_header = query_params.get('__cookie')
    if cookie_header:
        cookie = SimpleCookie(cookie_header[0])
        if key in cookie:
            return cookie[key].value
    return None

def delete_cookie(key):
    cookie = SimpleCookie()
    cookie[key] = ''
    cookie[key]['path'] = '/'
    cookie[key]['max-age'] = 0
    cookie[key]['expires'] = 'Thu, 01 Jan 1970 00:00:00 GMT'
    st.experimental_set_query_params(__cookie=str(cookie.output(header='', sep='')).strip())

# 세션 상태 초기화
if "logged_in" not in st.session_state:
    st.session_state["logged_in"] = False
    st.session_state["user_id"] = None

# 쿠키에서 로그인 상태 확인
if not st.session_state["logged_in"]:
    user_id_cookie = get_cookie("user_id")
    if user_id_cookie:
        st.session_state["logged_in"] = True
        st.session_state["user_id"] = user_id_cookie

#------------------------------------------------------------
        
# API 호출 함수
def fetch_data(api_url):
    response = requests.get(api_url)
    if response.status_code == 200:
        data = response.json()
        return pd.DataFrame(data)
    else:
        st.error(f"Failed to fetch data from {api_url}")
        return pd.DataFrame()
    
    
# SARIMA 모델을 사용한 예측 함수
def forecast_sarima(data, steps=24, seasonal_order=(1, 1, 1, 24)):
    model = SARIMAX(data, order=(1, 1, 1), seasonal_order=seasonal_order)
    model_fit = model.fit(disp=False)
    forecast = model_fit.get_forecast(steps=steps)
    mean_forecast = forecast.predicted_mean
    mean_forecast = mean_forecast.apply(lambda x: max(x, 0))  # 음수를 0으로 처리
    conf_int = forecast.conf_int()
    conf_int = conf_int.applymap(lambda x: max(x, 0))  # 신뢰 구간도 음수를 0으로 처리
    return mean_forecast, conf_int


#------------------------------------------------------------

st.markdown("""
        <style>
        .reportview-container {
            background: #f5f5f5;
            color: #000000;
        }
        .sidebar .sidebar-content {
            background: #ffffff;
            color: #000000;
        }
        @font-face {
            font-family: 'MyFont';
            src: url('http://localhost:8000/assets/Blobtastics.ttf') format('truetype');
        }
        .custom-font {
            font-family: 'MyFont', sans-serif;
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .header img {
            width: 100%;
            height: auto;
        }
        .subheader {
            font-size: 1.5em;
            margin-bottom: 20px;
        }
        /* 사이드바 타이틀 폰트 적용 */
        .sidebar .sidebar-content h1 {
            font-family: 'MyFont', sans-serif;
        }
        </style>
    """, unsafe_allow_html=True)

    
# pages
def show_login_page():
    st.title("Login Page")

    # 사용자 입력 받기
    username = st.text_input("Username")
    password = st.text_input("Password", type="password")

    if st.button("Login"):
        if username and password:
            # Django API 엔드포인트
            api_url = base_url + "login"
            
            # API 요청 보내기
            response = requests.post(api_url, data={'username': username, 'password': password})
            
            # 응답 데이터 확인
            if response.status_code == 200:
                data = response.json()
                if data.get("status") == "success":
                    st.session_state["user_id"] = data.get("user_id")
                    st.session_state["logged_in"] = True
                    set_cookie("user_id", data.get("user_id"))
                    st.experimental_rerun()
                else:
                    st.error("Username or password is incorrect.")
            elif response.status_code == 401:
                data = response.json()
                st.error(data.get("message"))
            else:
                st.error("Failed to connect to the API.")
        else:
            st.warning("Please enter both username and password.")

# 메인 페이지 함수
def main_page():

    st.markdown("""
        <div class="header">
            <h1 class="custom-font">Admin Dashboard</h1>
        </div>
    """, unsafe_allow_html=True)
    
     # 이미지와 텍스트를 나누는 레이아웃 설정
    col1, col2 = st.columns([1, 2])  # col1이 좁고 col2가 넓게 설정

    with col1:
        st.image("assets/photos/plain.png", caption="Standing Yum", use_column_width=False, width=200)
    
    with col2:
        st.markdown("""
            <h3>Welcome to the Admin Dashboard</h3>
            <p style='font-size:18px;'>
            Here you can monitor user activity and gain insights into how players interact with the game. 
            Use the graphs to see the number of users active during different hours of the day, 
            and leverage forecasts to plan for peak times.
            </p>
            <p style='font-size:18px;'>
            The dashboard also provides detailed analysis of item usage within the game. 
            By understanding how and when players use certain items, you can make informed decisions 
            to improve game balance and enhance the overall gaming experience. 
            Utilize these insights to optimize item availability and effectiveness.
            </p>
        """, unsafe_allow_html=True)


# 유저 접속 시간대별 활동 페이지
def show_user_activity_page():
    st.title("Hourly User Activity and Forecast")

    st.markdown("""
        <p style='font-size:18px;'>
        This graph allows you to monitor the number of users active during different hours of the day. 
        
        By using the SARIMA model, we can also forecast the expected number of users for each hour, 
        helping you to plan for peak times and optimize your game operations accordingly.
        </p>
    """, unsafe_allow_html=True)

    # API 엔드포인트에서 데이터 가져오기
    api_url = base_url + "turn/start"
    df = fetch_data(api_url)

    if not df.empty:
        # 날짜 변환 및 시간대 추출
        df['turn_start'] = pd.to_datetime(df['turn_start'], format='ISO8601')
        df['hour'] = df['turn_start'].dt.hour

        # 시간대별 유저 수 계산
        hourly_activity = df.groupby('hour').size().reset_index(name='user_count')

        # 데이터가 충분한지 확인
        if len(hourly_activity) > 10:
            # SARIMA 예측
            forecast, conf_int = forecast_sarima(hourly_activity['user_count'])
            forecast_index = (hourly_activity['hour'].iloc[-1] + 1) % 24
            forecast_hours = [(forecast_index + i) % 24 for i in range(len(forecast))]
            forecast_df = pd.DataFrame({'hour': forecast_hours, 'user_count': forecast})

            # 실제 데이터와 예측 데이터 합치기
            combined_df = pd.concat([hourly_activity, forecast_df])

            fig = go.Figure()
            fig.add_trace(go.Bar(x=hourly_activity['hour'], y=hourly_activity['user_count'], name='Actual', marker_color='#636EFA'))
            fig.add_trace(go.Scatter(x=forecast_df['hour'], y=forecast_df['user_count'], mode='lines+markers', name='Forecast', line=dict(color='#EF553B')))

            fig.update_layout(
                title='User Activity by Hour (Actual vs Forecast)',
                xaxis_title='Hour of the Day',
                yaxis_title='Number of Users',
                legend=dict(x=0.01, y=0.99)
            )

            st.plotly_chart(fig)
        else:
            st.write("Not enough data for SARIMA forecasting")
    else:
        st.write("No data available")



def show_playtime_item_analysis_page():    
    st.title("Item Usage by Game Duration")

    st.write("""
        <div style="font-size:18px;">
        This section provides an analysis of item usage over the duration of the game. The graphs below show how often each item is used at different times during the game. Additionally, SARIMA models are used to forecast future item usage based on the current data.
        </div>
    """, unsafe_allow_html=True)

    # 각 아이템의 API 엔드포인트
    api_urls = {
        "slow down": base_url + "item1/pressed",
        "no bomb": base_url + "item2/pressed",
        "big size": base_url + "item3/pressed",
        "triple points": base_url + "item4/pressed"
    }

    for item, api_url in api_urls.items():
        df = fetch_data(api_url)
        if not df.empty:
            # pressed_ts를 timedelta로 변환
            df['pressed_ts'] = pd.to_timedelta(df['pressed_ts'])

            # 데이터의 분포를 확인하여 적절한 주기를 설정
            total_seconds = df['pressed_ts'].dt.total_seconds()
            quantiles = total_seconds.quantile([0.25, 0.5, 0.75])
            q1, median, q3 = quantiles[0.25], quantiles[0.5], quantiles[0.75]

            # 주기를 15분, 30분, 1시간 등으로 나눔
            if q3 <= 3600:
                df['period'] = total_seconds // 900  # 15분 단위
                period_unit = "15 minutes"
                periods_per_day = 24 * 4  # 하루에 96개의 15분 단위가 있음
            elif q3 <= 7200:
                df['period'] = total_seconds // 1800  # 30분 단위
                period_unit = "30 minutes"
                periods_per_day = 24 * 2  # 하루에 48개의 30분 단위가 있음
            else:
                df['period'] = total_seconds // 3600  # 1시간 단위
                period_unit = "1 hour"
                periods_per_day = 24  # 하루에 24개의 1시간 단위가 있음

            df['period'] = df['period'].astype(int)

            # 각 주기별 사용 빈도
            period_usage = df.groupby('period').size().reset_index(name='count')

            # 데이터가 충분한지 확인
            if len(period_usage) > 10:
                # SARIMA 예측
                forecast, conf_int = forecast_sarima(period_usage['count'])
                forecast_index = period_usage['period'].iloc[-1] + 1
                forecast_periods = [(forecast_index + i) % periods_per_day for i in range(len(forecast))]
                forecast_df = pd.DataFrame({'period': forecast_periods, 'count': forecast})

                # 실제 데이터와 예측 데이터 합치기
                combined_df = pd.concat([period_usage, forecast_df])

                fig = go.Figure()
                fig.add_trace(go.Bar(x=period_usage['period'].tolist(), y=period_usage['count'].tolist(), name='Actual', marker_color='#636EFA'))
                fig.add_trace(go.Scatter(x=forecast_df['period'].tolist(), y=forecast_df['count'].tolist(), mode='lines+markers', name='Forecast', line=dict(color='#EF553B')))

                fig.update_layout(
                    title=f'Usage of {item} by {period_unit} (Actual vs Forecast)',
                    xaxis_title=f'Period ({period_unit})',
                    yaxis_title='Frequency',
                    legend=dict(x=0.01, y=0.99)
                )

                st.plotly_chart(fig)



def show_item_user_analysis_page():    
    st.title("User Item Distribution")

    st.write("""
        <div style="font-size:18px;">
        This section provides an analysis of the distribution of item ownership among users. The graphs below show the number of users holding different quantities of each item. This helps in understanding the spread and popularity of items among the player base.
        </div>
    """, unsafe_allow_html=True)

    # 유저 정보를 가져올 API 엔드포인트
    api_url = base_url + "users"
    df = fetch_data(api_url)

    if not df.empty:
        # 각 아이템별 보유량에 따른 유저 수를 시각화
        items = ["item_slow_down", "item_no_bomb", "item_big_size", "item_triple_points"]
        
        for item in items:
            item_counts = df[item].value_counts().reset_index()
            item_counts.columns = [item, 'user_count']
            fig = px.bar(
                item_counts,
                x=item,
                y='user_count',
                title=f'User Count by {item}',
                labels={item: 'Quantity', 'user_count': 'User Count'},
                color_discrete_sequence=["#636EFA"]
            )
            st.plotly_chart(fig)
        

def show_best_score_distribution_page():    
    st.title("Best Score Analysis")

    st.write("""
        <div style="font-size:18px;">
        This section provides an analysis of the distribution of users based on their best scores. The first graph below shows the number of users who have achieved different best scores. This helps in understanding the skill levels and progress of the player base.
        <br><br>
        Additionally, the second graph explores the correlation between users' best scores and their total item counts. By examining this relationship, we can gain insights into how item usage might influence player performance.
        </div>
    """, unsafe_allow_html=True)
    # 유저 정보를 가져올 API 엔드포인트
    
    api_url = base_url + "users"
    df = fetch_data(api_url)

    if not df.empty:
        # 베스트 스코어별 유저 수를 시각화
        best_score_counts = df['best_score'].value_counts().reset_index()
        best_score_counts.columns = ['best_score', 'user_count']
        best_score_counts = best_score_counts.sort_values(by='best_score')  # best_score를 기준으로 정렬
        
        fig = px.bar(
            best_score_counts,
            x='best_score',
            y='user_count',
            title='User Count by Best Score',
            labels={'best_score': 'Best Score', 'user_count': 'User Count'},
            color_discrete_sequence=["#636EFA"]
        )
        st.plotly_chart(fig)
        
        
        # 아이템 보유량과 베스트 스코어의 상관관계를 시각화
        fig = px.density_heatmap(
            df, 
            x='item_count', 
            y='best_score', 
            title='Item Count vs Best Score Correlation',
            labels={'item_count': 'Item Count', 'best_score': 'Best Score'},
            color_continuous_scale='Viridis'
        )
        st.plotly_chart(fig)

def show_user_management_page():    
    st.title("User Management")
    st.write("""
            <div style="font-size:18px;">
            This section allows administrators to manage user accounts, roles, and permissions. The user management interface provides a comprehensive view of all registered users, enabling you to oversee and control user-related settings effectively. Below, you will find a table displaying current user information, excluding sensitive data such as passwords.
            <br><br>
            </div>
        """, unsafe_allow_html=True)
    api_url = base_url + "users"
    df = fetch_data(api_url)
    
    if 'password' in df.columns:
        df = df.drop(columns=['password'])
    
    st.dataframe(df)


def show_query_page():    
    st.title("Custom Database Query Executor")

    st.write("""
        <div style="font-size:18px;">
        This section allows you to execute custom SQL queries directly on the database. Simply enter your query in the text area below and click 'Execute Query'. The results will be displayed in a tabular format below, enabling you to analyze and manipulate data as needed.
        <br><br>
        <span style="color:#FF6347;">
        Note: Please ensure that your query is properly formatted and valid to avoid errors. Sensitive information such as passwords will be excluded from the results.
        </span>
        </div>
    """, unsafe_allow_html=True)


    # 유저 정보를 가져올 API 엔드포인트
    api_url = base_url + "query"
    

    query = st.text_area("Enter your SQL query here:")

    if st.button("Execute Query"):
        if query:
            response = requests.post(api_url, json={"query": query})
            
            if response.status_code == 200:
                result = response.json().get("result")
                st.success("Query executed successfully!")
                
                # 결과를 데이터프레임으로 변환하여 화면에 표시
                if result:
                    df = pd.DataFrame(result)
                    if 'password' in df.columns:
                        df = df.drop(columns=['password'])
                    st.dataframe(df)
                else:
                    st.write("No results returned from the query.")
            else:
                st.error(f"Error: {response.json().get('error')}")
        else:
            st.warning("Please enter a query.")
            



def show_visualization_page():
    st.sidebar.markdown("""
        <h1 class="custom-font">Admin Dashboard</h1>
    """, unsafe_allow_html=True)
    
    # 페이지 선택을 위한 selectbox
    page = st.sidebar.selectbox(
        "Select Page",
        ["Main", "Hourly User Activity and Forecast", "Item Usage by Game Duration", "User Item Distribution", "Best Score Analysis", "User Management", "Custom Database Query Executor"]
    )
    
    if st.sidebar.button("Logout"):
        st.session_state["logged_in"] = False
        st.session_state["user_id"] = None
        delete_cookie("user_id")
        st.experimental_rerun()
        
    if page == "Main":
        main_page()
    elif page == "Hourly User Activity and Forecast":
        show_user_activity_page()
    elif page == "Item Usage by Game Duration":
        show_playtime_item_analysis_page()
    elif page == "User Item Distribution":
        show_item_user_analysis_page()
    elif page == "Best Score Analysis":
        show_best_score_distribution_page()
    elif page == "User Management":
        show_user_management_page()
    elif page == "Custom Database Query Executor":
        show_query_page()


# 로그인 상태에 따라 페이지 전환
if st.session_state["logged_in"]:
    show_visualization_page()
else:
    show_login_page()
