import streamlit as st
import requests
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import seaborn as sns
from http.cookies import SimpleCookie
from statsmodels.tsa.arima.model import ARIMA


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
        
        
def fetch_data(api_url):
    response = requests.get(api_url)
    if response.status_code == 200:
        data = response.json()
        return pd.DataFrame(data)
    else:
        st.error(f"Failed to fetch data from {api_url}")
        return pd.DataFrame()
    
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

def show_overview_page():
    st.title("Overview of Game Data")

    # 더미 데이터 생성
    data = {
        "item": ["item1", "item2", "item3", "item4", "item5", "item6"],
        "value": [10, 23, 15, 30, 45, 22],
        "category": ["A", "B", "A", "B", "A", "B"]
    }
    df = pd.DataFrame(data)

    # 기본 데이터 프레임 표시
    st.write("Sample Data:")
    st.dataframe(df)

    # 레이아웃 설정
    col1, col2 = st.columns(2)

    with col1:
        # Plotly 바 차트
        st.subheader("Bar Chart")
        fig = px.bar(df, x="item", y="value", title="Item Values", labels={"value": "Value", "item": "Item"})
        st.plotly_chart(fig)

    with col2:
        # Plotly 히트맵
        st.subheader("Heatmap")
        pivot_df = df.pivot(index="item", columns="category", values="value")
        
        heatmap = go.Figure(data=go.Heatmap(
            z=pivot_df.values,
            x=pivot_df.columns,
            y=pivot_df.index,
            colorscale='Viridis'
        ))
        
        heatmap.update_layout(title="Value Heatmap", xaxis_nticks=36)
        st.plotly_chart(heatmap)
        

# ARIMA 모델을 사용한 예측 함수
def forecast_arima(data, steps=24):
    model = ARIMA(data, order=(5, 1, 0))
    model_fit = model.fit()
    forecast = model_fit.forecast(steps=steps)
    return forecast

def show_playtime_item_analysis_page():
    st.title("Playtime-Item Analysis")

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
            # 날짜 변환 시 format 인자 추가
            df['pressed_ts'] = pd.to_datetime(df['pressed_ts'], format='ISO8601')
            
            # 시간별 사용 빈도 그래프
            df['hour'] = df['pressed_ts'].dt.hour
            hourly_usage = df.groupby('hour').size().reset_index(name='count')
            
            # 데이터가 충분한지 확인
            if len(hourly_usage) > 10:
                # ARIMA 예측
                forecast = forecast_arima(hourly_usage['count'])
                forecast_index = pd.date_range(start=hourly_usage['hour'].iloc[-1], periods=len(forecast), freq='H').hour
                forecast_df = pd.DataFrame({'hour': forecast_index, 'count': forecast})
                
                # 실제 데이터와 예측 데이터 합치기
                combined_df = pd.concat([hourly_usage, forecast_df])
                
                fig = go.Figure()
                fig.add_trace(go.Bar(x=hourly_usage['hour'], y=hourly_usage['count'], name='Actual', marker_color='#636EFA'))
                fig.add_trace(go.Scatter(x=forecast_df['hour'], y=forecast_df['count'], mode='lines+markers', name='Forecast', line=dict(color='#EF553B')))
                
                fig.update_layout(
                    title=f'Usage of {item} by Hour (Actual vs Forecast)',
                    xaxis_title='Hour of the Day',
                    yaxis_title='Frequency',
                    legend=dict(x=0.01, y=0.99)
                )
                
                st.plotly_chart(fig)
            
            # 날짜별 사용 빈도 꺾은선 그래프
            df['date'] = df['pressed_ts'].dt.date
            daily_usage = df.groupby('date').size().reset_index(name='count')
            line_fig = px.line(
                daily_usage, 
                x='date', 
                y='count', 
                title=f'Daily Usage of {item}', 
                labels={"date": "Date", "count": "Usage Count"},
                markers=True,
                color_discrete_sequence=["#EF553B"]
            )
            st.plotly_chart(line_fig)


def show_item_user_analysis_page():
    st.title("User Item Distribution")

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
    st.title("Best Score Distribution")

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
        
def show_item_best_score_correlation_page():
    st.title("Item Count vs Best Score Correlation")

    # 유저 정보를 가져올 API 엔드포인트
    api_url = base_url + "users"
    df = fetch_data(api_url)

    if not df.empty:
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

def show_query_page():
    st.title("Database Query Executor")

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
    

def show_kpi_page():
    st.title("Key Performance Indicators")

    # 더미 데이터 생성
    data = {
        "item": ["item1", "item2", "item3", "item4", "item5", "item6"],
        "value": [10, 23, 15, 30, 45, 22]
    }
    df = pd.DataFrame(data)

    # 대시보드 형태의 KPI 표시
    kpi1, kpi2, kpi3 = st.columns(3)

    with kpi1:
        st.metric(label="Total Value", value=df["value"].sum())

    with kpi2:
        st.metric(label="Average Value", value=round(df["value"].mean(), 2))

    with kpi3:
        st.metric(label="Max Value", value=df["value"].max())

def show_user_management_page():
    st.title("User Management")
    st.write("Here you can manage user accounts, roles, and permissions.")
    # 더미 데이터 예시
    user_data = {
        "username": ["user1", "user2", "user3"],
        "role": ["admin", "player", "player"],
        "status": ["active", "inactive", "active"]
    }
    user_df = pd.DataFrame(user_data)
    st.dataframe(user_df)

def show_visualization_page():
    st.sidebar.title("Admin Dashboard")
    
    # 페이지 선택을 위한 selectbox
    page = st.sidebar.selectbox(
        "Select Page",
        ["Overview", "Detailed Analysis", "User Item Distribution", "Best Score Distribution", "Item Best score Correlation", "Query", "KPI", "User Management"]
    )
    
    if st.sidebar.button("Logout"):
        st.session_state["logged_in"] = False
        st.session_state["user_id"] = None
        delete_cookie("user_id")
        st.experimental_rerun()

    if page == "Overview":
        show_overview_page()
    elif page == "Detailed Analysis":
        show_playtime_item_analysis_page()
    elif page == "User Item Distribution":
        show_item_user_analysis_page()
    elif page == "Best Score Distribution":
        show_best_score_distribution_page()
    elif page == "Item Best score Correlation":
        show_item_best_score_correlation_page()
    elif page == "Query":
        show_query_page()
    elif page == "KPI":
        show_kpi_page()
    elif page == "User Management":
        show_user_management_page()

# 로그인 상태에 따라 페이지 전환
if st.session_state["logged_in"]:
    show_visualization_page()
else:
    show_login_page()
