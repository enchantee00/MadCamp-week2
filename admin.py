import streamlit as st
import pandas as pd
import yaml
from yaml.loader import SafeLoader
import streamlit_authenticator as stauth

# Load config file
with open('config.yaml', 'r', encoding='utf-8') as file:
    config = yaml.load(file, Loader=SafeLoader)

# Create the authenticator object
authenticator = stauth.Authenticate(
    config['credentials'],
    config['cookie']['name'],
    config['cookie']['key'],
    config['cookie']['expiry_days'],
    config['pre-authorized']
)

def main():
    st.title("Admin Page")

    # If authentication status is not in session state, set it to None
    if 'authentication_status' not in st.session_state:
        st.session_state.authentication_status = None

    # If authentication status is None or False, show login form
    if st.session_state.authentication_status is None or st.session_state.authentication_status is False:
        name, authentication_status, username = authenticator.login("Login", "main")

        if authentication_status:
            st.session_state.authentication_status = authentication_status
            st.session_state.username = username
            st.session_state.name = name
            st.experimental_rerun()
        elif authentication_status is False:
            st.error("Username/password is incorrect")
        elif authentication_status is None:
            st.warning("Please enter your username and password")

    # If authenticated, show main content
    if st.session_state.authentication_status:
        authenticator.logout("Logout", "sidebar")
        st.sidebar.title(f"Welcome {st.session_state.name}")

        menu = ["Home", "Manage Users", "View Data"]
        choice = st.sidebar.selectbox("Menu", menu)
        
        if choice == "Home":
            st.subheader(f"Welcome to the Admin Page, {st.session_state.name}")
            st.write("Use the sidebar to navigate.")
        
        elif choice == "Manage Users":
            st.subheader("Manage Users")
            st.write("User management functionality goes here")
        
        elif choice == "View Data":
            st.subheader("View Data")
            st.write("Here you can view and analyze data.")
            
            data = pd.DataFrame({
                'Parameter A': [10, 20, 30, 40, 50],
                'Parameter B': [100, 200, 300, 400, 500]
            })
            
            st.dataframe(data)
            st.line_chart(data.set_index('Parameter A'))

if __name__ == '__main__':
    main()
