import streamlit as st
import requests
import pandas as pd
import matplotlib.pyplot as plt
from http.cookies import SimpleCookie

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

def show_login_page():
    st.title("Login Page")

    # 사용자 입력 받기
    username = st.text_input("Username")
    password = st.text_input("Password", type="password")

    if st.button("Login"):
        if username and password:
            # Django API 엔드포인트
            api_url = "http://172.10.7.97:80/api/login/"
            
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

def show_visualization_page():
    st.title("Data Visualization Page")
    
    if st.button("Logout"):
        st.session_state["logged_in"] = False
        st.session_state["user_id"] = None
        delete_cookie("user_id")
        st.experimental_rerun()

    # 여기에 데이터 시각화 코드 추가
    # 더미 데이터 예시
    data = {
        "item": ["item1", "item2", "item3", "item4"],
        "value": [10, 23, 15, 30]
    }
    df = pd.DataFrame(data)

    st.write("Sample Data:")
    st.dataframe(df)

    st.write("Bar Chart:")
    fig, ax = plt.subplots()
    ax.bar(df["item"], df["value"])
    st.pyplot(fig)

# 로그인 상태에 따라 페이지 전환
if st.session_state["logged_in"]:
    show_visualization_page()
else:
    show_login_page()
