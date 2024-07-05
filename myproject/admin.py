# app.py

import streamlit as st
import pandas as pd
import requests

# Streamlit 애플리케이션 제목
st.title("Hello Streamlit")

# Django API 엔드포인트
api_url = "http://localhost:8000/api/items/"

# API 요청 보내기
response = requests.get(api_url)

# 응답 데이터 확인
if response.status_code == 200:
    data = response.json()
    df = pd.DataFrame(data)
    st.write("Here is the data from the Django API:")
    st.dataframe(df)
else:
    st.error("Failed to fetch data from the API.")
