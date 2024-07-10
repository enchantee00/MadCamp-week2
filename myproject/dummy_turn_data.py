import random
import pymysql
from datetime import datetime, timedelta

# MySQL 데이터베이스에 연결합니다.
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='password',
    database='game_db'
)
cursor = conn.cursor()

# EventTurn 데이터 생성
turn_ids = []
turn_start_base = datetime(2024, 7, 8, 0, 0, 0)
for i in range(1, 1000):  # 1000개의 turn 데이터 생성
    user_id = 1
    # 저녁 시간대(18:00 ~ 23:59)에 더 많은 이벤트가 발생하도록 편향된 데이터 생성
    if random.random() < 0.7:  # 70% 확률로 저녁 시간대
        turn_start = turn_start_base.replace(hour=random.randint(18, 23), minute=random.randint(0, 59))
    else:  # 나머지 시간대
        turn_start = turn_start_base.replace(hour=random.randint(0, 17), minute=random.randint(0, 59))

    turn_duration = random.randint(5_000_000, 600_000_000)  # 5분에서 1시간 사이의 게임 시간 (마이크로초)

    cursor.execute(
        "INSERT INTO event_turn (user_id, turn_duration, turn_start) VALUES (%s, %s, %s)",
        (user_id, turn_duration, turn_start)
    )
    turn_ids.append(cursor.lastrowid)
    
# EventItem 데이터 생성 함수
def create_event_item_data(item_table, turn_ids):
    for turn_id in turn_ids:
        user_id = 1
        # 아이템 사용이 게임 시작 후 1분 이내에 더 많이 발생하도록 편향된 데이터 생성
        if random.random() < 0.3:  # 70% 확률로 게임 시작 후 1분 이내
            pressed_ts = random.randint(5_000_000, 60_000_000)  # 5초에서 1분 사이의 마이크로초 값
        else:  # 나머지 시간대
            pressed_ts = random.randint(60_000_000, 600_000_000)  # 1분에서 10분 사이의 마이크로초 값

        cursor.execute(
            f"INSERT INTO {item_table} (turn_id, user_id, pressed_ts) VALUES (%s, %s, %s)",
            (turn_id, user_id, pressed_ts)
        )

# 각 아이템 테이블에 데이터 생성
create_event_item_data('event_item_slow_down', turn_ids)
create_event_item_data('event_item_no_bomb', turn_ids)
create_event_item_data('event_item_big_size', turn_ids)
create_event_item_data('event_item_triple_points', turn_ids)

# 변경사항 커밋
conn.commit()

# 연결 종료
cursor.close()
conn.close()
