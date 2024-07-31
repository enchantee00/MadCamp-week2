
## YumYum !

---

<img src="https://github.com/user-attachments/assets/987b6d3f-f439-4243-8cbb-9795810c57af" width="30%">

> ***하늘에서 음식이 내린다면?***
> 
- 떨어지는 음식을 놓치지 마세요!
- 폭탄이 떨어진다면 피하세요!
- 아이템을 사용해 파워업하세요!

<aside>
    
😋 **More for Developers!**

---

게임 이용자의 데이터를 실시간으로 DB에 저장하고,

실시간 데이터를 분석하여 인사이트를 얻을 수 있는

**`Developer Tool`**을 연동된 웹페이지에서 함께 만나보세요!

</aside>

### Team

---

|      정지윤       |          김이겸         |                                                                                                       
| :------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------: |
|   <img width="100px" src="https://github.com/enchantee00/MadCamp-week1/assets/62553866/67b24f11-2c48-4704-ac34-6c3be41c9982" />    |                      <img width="100px" src="https://github.com/user-attachments/assets/5837356f-e9d2-4137-abd0-8bb8255b5efb" />    |
|   [@enchantee00](https://github.com/enchantee00)   |    [@inthelongestday](https://github.com/inthelongestday)  |
| 부산대학교 정보컴퓨터공학부 3학년 | 카이스트 전산학부 4학년 |

<br>
<br>   

### Tech Stack

---

**Android :** Java

**Frontend :** Python

**Server Framework** : Django

**Web Framework :** Streamlit

**Database :** MySQL

**IDE :** Android Studio / VSCode

<br>
<br>

### Introduction

---

> **Preview**
> 

---

<p align="center">  
    <img src="https://github.com/user-attachments/assets/423d0de8-7761-47d3-bfc3-43961969f3cf" align="center" width="20%">  
    <img src="https://github.com/user-attachments/assets/334456cd-4b32-4a8e-b94e-13c885583e90" align="center" width="60%">  
</p>
<br>

> **Experience**
> 

---

🍕 **YumYum! 은 귀엽고 간단한 게임입니다!**

게임의 마스코트 **`Yum`** 이 떨어지는 음식을 놓치지 않고 먹을 수 있게 움직여 주세요.

**`YumYum!`** 을 통해 점점 어려워지는 레벨, 떨어지는 폭탄과 다양한 아이템을 즐겨보세요.

하지만 개발자의 일은 게임 출시에 그치지 않습니다.

🍕 **YumYum! 의 실시간 데이터를 조회하고 분석하여 게임을 성장시킬 수 있는 Developer Tool,**

개발자 / 관리자용 Admin Page를 만나보세요.

<aside>
🧑🏻‍💻 Admin Page 는 이러한 데이터 분석을 위해 아래와 같은 기능을 제공합니다.

**Data Stream**

- 게임에서 유저의 행동을 관찰할 수 있도록 실시간 이벤트 데이터를 정의합니다.
- 실시간 이벤트 데이터를 서버 내 구축된 DB에 저장합니다.

**Admin Page**

- (저희가 임의로 판단한,) 인사이트를 추출하기에 용이한 분석 차트를 제공합니다.
- 관리자의 선택에 따라 추가로 확인하고 싶은 데이터가 있다면, 직접 쿼리(Query)를 작성하여 서버에 Request를 보낼 수 있습니다.
    - 결과는 테이블 형식으로 조회 가능합니다.
- 최신 유저 데이터를 반영하기 위한 새로고침 기능과, 분석 후 인사이트를 적을 수 있는 메모 기능을 제공합니다.
</aside>

YumYum! 과 Admin Page를 통해

유저의 행동 패턴을 게임 내 이벤트 데이터를 통해 분석하여 유저의 유입을 유도하거나,

게임의 난이도 조절, 새로운 아이템 출시 및 수정 등 보다 효율적인 성장을 이뤄낼 수 있습니다.

결제 페이지와 연동하게 된다면 게임의 수익구조를 개선시키기 위한 데이터 분석도 가능합니다.

😋 **쉽고 재미있는 게임과 이를 성장시킬 수 있는 데이터 분석 페이지를 만나보세요!** 🍕

<br>
<br>

### Details

---

<aside>
    
🕹️ **YumYum! - Game**

</aside>

> **Splash Page**
> 

---

- 게임을 시작하면 다음과 같이 귀여운 랜딩 페이지가 보입니다.
- 음식 이미지는 실제 게임 플레이에도 활용됩니다.

![Landing_Page](https://github.com/user-attachments/assets/aa963354-fe70-4be8-8f99-e5926f02d476)

<br>

> **Login Page**
> 

---

- 로그인 페이지에서 username과 password를 입력하고 login 버튼을 누르면 서버에 user 정보를 확인하는 request를 보냅니다.
- 서버에서는 request를 통해 전달받은 user 입력 정보를 db에 등록된 user 데이터베이스에서 조회하여 가입된 사용자인 경우 ok code와 해당 user의 기존 정보를 response로 반환합니다.
- 앱은 로그인한 유저 정보를 받아 앱 전체에서 사용할 수 있도록 UserDTO에 저장한 후 메인 페이지로 이동합니다.

![Login](https://github.com/user-attachments/assets/507071fe-010d-4695-bb77-a9895c06bc8a)

<br>

> **How is the Weather?**
> 

---

게임을 플레이하는 디바이스의 현재 위치를 받아와 (내장 GPS),

공공데이터포털 API를 활용해 현재 위치의 실황 날씨를 화면으로 보여줍니다.

아래와 같이 네 가지 경우로 메인화면과 게임 플레이화면이 변경됩니다.

<p align="center">
    <img src="https://github.com/user-attachments/assets/68bba708-eea9-4990-9d56-9fb02ad5d44b" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/eb647f61-5883-4882-bbd2-3e373cbf6e8f" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/bf237a65-539f-4bf3-87f5-0a7884ed45f3" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/eda5d3fd-77f5-4e49-b82a-0bbe79e0e2fe" align="center" width="20%">
</p>

<p align="center">
    <img src="https://github.com/user-attachments/assets/4ccf296d-c3a2-4539-b367-987e0a33e326" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/c4bbc3d3-4999-41f5-a14d-cd335c79fc40" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/1b58027c-53fc-4bbe-ab89-cf022159d579" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/e18bae56-3e60-4a99-898c-d2624bee39db" align="center" width="20%">
</p>

<br>

> **Game Play**
> 

---

**`Basic Rules`**

- Start 버튼을 누르면 게임 플레이 화면이 나타납니다.
- 플레이화면 좌측 상단에는 이번 플레이의 점수가, 우측 상단에는 잔여 라이프 수가 하트 이미지로 표현되어 있습니다.
- 게임의 마스코트 캐릭터 **`Yum`**을 드래그하여 움직일 수 있습니다. 캐릭터를 눌러 드래그하면 캐릭터가 떨어지는 음식을 먹기 위해 입을 벌립니다!
- **`Yum`**을 움직여 떨어지는 음식을 놓치지 말고 먹어야 합니다.
    - 음식 하나를 먹을 때마다 점수가 10점씩 올라갑니다.
    - 음식을 하나 놓칠 경우, 라이프가 하나 소모됩니다.
    
<img src="https://github.com/user-attachments/assets/b32c7316-9490-434c-8a49-cbfbced97ccd" width="30%">

- 라이프가 5개 전부 소모되면, Game Over입니다.
    - Game Over 화면에서 Restart 버튼을 누르면 게임을 다시 시작할 수 있습니다.
    - 이미 소모한 아이템 보유량은 원복되지 않습니다.
    - 뒤로가기 버튼을 누르면, 메인 화면으로 돌아갈 수 있습니다.

<img src="https://github.com/user-attachments/assets/91186dc9-aa6e-40a6-82d4-d3e25aa0fd82" width="30%">


**`Items`**

- 우측 모서리에는 사용 가능한 아이템이 표시됩니다.
- 아이템 위쪽에는 유저의 해당 아이템 보유량이 표시되어 있습니다.
- 아이템 사용 중에는 해당 아이템이 회색으로 처리되어, 중복으로 사용할 수는 없습니다.
- 아이템의 1회 유지 시간은 10초입니다. 아이템 사용이 끝난 후, 보유량이 남아있으면 아이템을 또 사용할 수 있습니다.
- 아이템을 전부 소모하면 아이템은 회색처리되어, 사용할 수 없습니다.
- 여러 아이템은 동시에, 중복 사용 가능합니다.

Item 1. Slow Down

---

떨어지는 음식의 속도가 느려집니다!

![Item_1 _Slow_Down](https://github.com/user-attachments/assets/1ed60aaa-9bfd-47e6-b725-fdb68446cb8f)

Item 2. No Bomb

---

Lv.4부터 적용 가능합니다.

떨어지는 폭탄을 막을 수 있는 방어막이 생성됩니다!

방어막이 활성화되는 10초 동안에는 폭탄을 맞아도 라이프가 소모되지 않습니다.

![Item_2 _No_Bomb](https://github.com/user-attachments/assets/ca01ceca-1b65-4aa5-9cf3-51c113b33451)

Item 3. Triple Points

---

음식 하나를 먹을 때 점수가 기존의 3배씩 (30 pts) 올라갑니다!

![Item_3 _Triple_Points](https://github.com/user-attachments/assets/0feabf0b-0c25-4685-bead-d70bc3173428)

Item 4. Bigger Food

---

떨어지는 음식의 크기가 2배 커집니다!

음식의 크기가 커지며 Yum 이 떨어지는 음식을 먹기 쉬워집니다.

![Item_4 _Bigger_Food](https://github.com/user-attachments/assets/fa464c0b-cb8d-4d19-ad13-05981e5ce661)

- **`Other Rules`**
    
    
    - 뒤로가기 버튼을 누르면 메인 화면으로 돌아갈 수 있습니다.
    - 게임 중간에 Stop 버튼을 누르면 플레이 중이던 게임을 끝낼 수 있습니다.
        - Stop 버튼을 누르지 않고 뒤로 가기 버튼을 누르면 게임 점수는 point에 반영되지 않습니다.
    - 게임이 끝나면 게임의 최종 점수는 유저의 Point에 반영됩니다.
    - 라이프 아래에는 현재 플레이 중인 레벨이 표시됩니다.
    - 레벨은 Lv.1부터 Lv.5까지로 이루어져 있으며, 특정 점수를 돌파할 때마다 레벨이 올라갑니다. (유저 자체의 레벨이 아닌, 게임 내의 난이도를 의미하는 레벨)
        - 레벨이 올라가면 음식이 떨어지는 속도가 빨라지고, 음식이 떨어지는 간격도 줄어듭니다.
        - Lv.4부터는 폭탄이 떨어집니다. 음식과 달리 떨어지는 폭탄은 피해야 합니다. 폭탄을 맞을 경우 폭탄 하나 당 라이프가 하나 소모됩니다.
    
<br>

> **Record**
> 

---

- 메인화면에서 Record 화면으로 넘어가면, 로그인한 유저의 정보를 볼 수 있습니다.
- 지금까지 기록한 Best Score, 지금까지 플레이한 점수 합산으로 계산되는 Point 를 보여줍니다.
- 보유한 Point로는 게임 플레이 시 필요한 아이템을 구입할 수 있습니다.
    - 한 번에 한 종류의 아이템을 여러 개씩 구입 가능합니다.
- 자신의 닉네임을 클릭하여 변경할 수 있습니다.
    - 추후에 로그인 시 변경된 닉네임으로 로그인해야 합니다.


<p align="center">
    <img src="https://github.com/user-attachments/assets/7f9fc3a8-a464-4244-a036-4d1e28185b37" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/8de755fd-db18-4d55-8eb3-4b3c70f38645" align="center" width="20%">
    <img src="https://github.com/user-attachments/assets/302fc304-35e8-488e-a453-82a77ab4f8de" align="center" width="20%">
</p>

<br>

> **Data Stream to Server / Database**
> 

---

YumYum! 게임에서 유저가 작동할 수 있는 여러 기능은 **이벤트로 정의**되어 **실시간으로 서버에 데이터를 전송**합니다.

Android의 Retrofit Client 를 사용하여 서버에 구축한 API를 사용해 서버로 Request를 전송합니다.

YumYum! 에서 정의한 이벤트는 다음과 같습니다.

- 게임 시작할 때
- 게임 플레이 중 아이템을 사용할 때 (4개의 아이템 각각 이벤트 정의)
- 게임을 종료할 때
    - 라이프를 전부 소모하여 Game Over 되었을 때
    - 유저가 매뉴얼하게 Stop 버튼을 눌러 해당 턴을 종료했을 때

이벤트 데이터 외에도, YumYum! 은 서버에 구축된 데이터베이스의 정보를 바탕으로 게임을 구성하고 유저의 정보를 조회하여 보여줍니다.

또한 게임이 한 턴 종료될 때마다 발생하는 이벤트를 서버에서 받아 처리하며, 반영되어야 할 정보를 데이터베이스 내 해당 유저 데이터에 업데이트합니다.

- Record 화면에서 username을 변경할 때
- 게임 종료 시 종료된 턴의 점수와 사용한 아이템 수량을 유저 정보에 반영할 때 등

<br>
<br>

<aside>
    
🧑🏻‍💻 **Developer / Admin Page**

</aside>

> **Login & Main Page**
> 

---
<p align="center">
<img width="70%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_6 58 06" src="https://github.com/user-attachments/assets/982542fd-5dcf-479c-804d-06212e258fee">
</p>

Admin Page 로그인 화면입니다. 현재 페이지에선 “users” 테이블의 “role” field 값이 “admin”인 계정(관리자)들만 로그인을 할 수 있습니다.

<p align="center">
<img width="70%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_6 57 27" src="https://github.com/user-attachments/assets/a6483cae-1cba-4657-965d-cabc360e505a">
</p>


admin dashboard의 메인 페이지입니다. 저희 게임의 마스코트인 Yum이 admin dashboard에선 어떤 일들을 할 수 있는지 알려주고 있습니다.

<br>

> **Data Analysis**
> 

---
<p align="center">
<img width="70%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 02 15" src="https://github.com/user-attachments/assets/ffc973e9-df31-46ab-a0cc-5b6e533de9db">
</p>


유저가 플레이하며 DB에 쌓인 게임 데이터를 바탕으로 데이터 분석을 할 수 있는 여러 그래프를 제공하고 있습니다.

> 저희가 다루는 데이터는 “접속 시간대”나 “게임 경과 시간” 등 **시계열 데이터**를 다루고 있고, 하루를 기준으로 데이터를 나타내고 있어서 “하루”라는 **주기**를 갖고 있습니다.

따라서 단순히 현재 그래프가 나타내고 있는 현 상황만 보여주는 것이 아니라, 미래 사용자 수나 미래 아이템 사용 횟수 정보를 제공한다면 개발자들이 양질의 인사이트를 얻어갈 수 있다고 판단하여 **시계열 모델을 사용**해 예측 정보 또한 그래프에 나타내고 있습니다.

**계절성(데이터가 일정 주기로 반복되는 패턴)**이 두드러지는 데이터를 다루고 있기 때문에 주기성을 반영하는 **SARIMA 모델**을 사용해 빨간색 꺾은선 그래프인 “Forecast”로 예측 정보를 표시했습니다.
> 

<p align="center">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 24 57" src="https://github.com/user-attachments/assets/7feaee5c-8893-4637-a1e8-1237db4b4647">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 26 13" src="https://github.com/user-attachments/assets/ec5f4910-9c45-499b-b66a-3479e665d2cb">
</p>

<p align="center">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 25 34" src="https://github.com/user-attachments/assets/cd622d0c-71b9-4002-b8b5-6e079f9d7ba4">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 26 35" src="https://github.com/user-attachments/assets/8345ab3c-8085-4a72-81d5-5ef993989848">
</p>

<br>

**Insights from our Admin Dashboard**

---

1. Hourly User Activity and Forecast
    
    시간대별로 유저 수를 나타낸 그래프입니다.
    
    개발자들은 해당 데이터를 통해 유저 수가 몰리는 시간대를 파악하여 특정 시간대에 게임을 하지 않겠냐는 푸쉬 알림을 날렸을 때 유저가 해당 알림에 의해 **게임에 접속할 수 있는 확률을 높일 수 있을 것입니다**.
    
2. Item Usage by Game Duration
    
    게임 경과 시간에 따른 해당 아이템을 누른 횟수를 나타낸 그래프입니다.
    
    개발자들은 게임 시작 후, 어느 정도 시간이 지났을 때에 아이템을 사용하는지 파악할 수 있습니다. 따라서 게임 도중 **유저가 가장 많이 아이템을 사용할 것 같은 시간대에 아이템을 추천해주는 등의 방식으로 아이템 구입을 유도할 수 있을 것입니다.**
    
3. User Item Distribution
    
    아이템 보유량에 따른 유저 수를 나타낸 그래프입니다.
    
    개발자들은 어떤 아이템이 유저에게 인기가 많은 지 한눈에 알아볼 수 있고, 이를 통해 **인기 많은 아이템을 분석해 새로운 아이템 개발에 도움을 얻을 수 있을 것입니다.**
    
4. Best Score Analysis
    
    최고 성적에 따른 유저 분포를 확인할 수 있는 그래프입니다.
    
    개발자들은 이를 통해 스코어 올리는 방식을 업데이트 하거나 현재 **게임의 난이도를 재설정 하는 데 도움을 얻을 수 있을 것입니다.**


<br>

**Additional Features**

---

<p align="center">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 51 54" src="https://github.com/user-attachments/assets/b382a94e-d10b-43bf-9688-d08285b2d223">
    <img width="40%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 52 12" src="https://github.com/user-attachments/assets/9e047705-f35a-4a04-be78-07518eaa5ca6">
</p>


추가적으로 전체 유저를 볼 수 있는 페이지를 제공해 비밀번호를 제외한 현재 게임 내 유저의 모든 정보를 열람 가능하게 했습니다.

위의 그래프와 표에도 불구하고 더 구체적인 데이터들을 확인하고 싶을 때! 
SQL문에 빠삭한 개발자들을 위한 자유 쿼리 페이지도 있어 문법만 맞다면 자유롭게 쿼리를 날려 원하는 데이터를 확인할 수 있습니다.

<br>

> **Memo**
> 

---

<img width="70%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_7 58 19" src="https://github.com/user-attachments/assets/bc19c367-c268-4cd2-8492-48d1171094f7">



각 그래프를 나타낸 페이지 하단에 **CRUD가 가능한 메모 기능**을 넣어 개발자들끼리 서로 메모를 남기며 소통할 수 있는 공간도 마련했습니다.

개발자들은 ‘Add Note’ 버튼을 통해 빈 메모를 만들고 내용을 자유롭게 적고 ‘Save Note’ 버튼으로 메모를 저장합니다. 맘에 들지 않는 메모가 있다면 ‘Delete Note’ 버튼으로 삭제까지 가능합니다.

<br>
<br>

<aside>
    
💽 **Server**

</aside>

> **Database Diagram**
> 

---

![MadCamp_week2-2](https://github.com/user-attachments/assets/a646f1f7-d518-4570-959a-5c382a954094)


저희 DB는 총 7개의 테이블로 이루어져 있습니다. “memos” 테이블을 제외한 모든 테이블이 앱과 관리자 페이지에 사용되고, “memos” 테이블은 관리자 페이지의 메모 기능을 위해 사용됩니다.

<br>

> **End Point**
> 

---

<img width="70%" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-07-10_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_8 16 01" src="https://github.com/user-attachments/assets/09d90bf7-54d6-4e46-85de-f52c3a593748">


API end point들입니다. “#app”으로 주석 처리한 하단 부분은 앱과 관련된 API이고, “#web”으로 주석 처리한 하단 부분은 관리자 페이지와 관련된 API입니다.

하나의 APIView class 안에 여러 http 메서드를 함께 담아 중복 코드를 최소화 하려고 노력했으며, 이를 통해 코드 가독성을 높였습니다.

<br>
<br>

### APK link

---

[YumYum.apk](https://drive.google.com/file/d/1DOK8YC0zymS7WiTFIKOcXw-vvwfZG_06/view?usp=drive_link)
