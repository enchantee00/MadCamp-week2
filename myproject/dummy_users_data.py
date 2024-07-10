import random
import string
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

# 랜덤한 문자열을 생성하는 함수입니다.
def random_string(length):
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(length))

# 사용자 데이터를 생성하는 함수입니다.
def create_user_data():
    username = random_string(8)
    password = random_string(12)
    role = 'player'
    play_count = random.randint(0, 100)
    best_score = random.randint(0, 1000)
    point = random.randint(0, 1000)
    item_count = random.randint(0, 50)
    item_slow_down = random.randint(0, 10)
    item_no_bomb = random.randint(0, 10)
    item_big_size = random.randint(0, 10)
    item_triple_points = random.randint(0, 10)
    total_duration = random.randint(0, 3600000)
    created_at = datetime.now() - timedelta(days=random.randint(0, 365))

    return (username, password, role, play_count, best_score, point, item_count, item_slow_down, item_no_bomb, item_big_size, item_triple_points, total_duration, created_at)

# 대량의 사용자 데이터를 생성하고 삽입합니다.
user_data_list = [create_user_data() for _ in range(1000)]

insert_query = """
INSERT INTO users (username, password, role, play_count, best_score, point, item_count, item_slow_down, item_no_bomb, item_big_size, item_triple_points, total_duration, created_at)
VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
"""

cursor.executemany(insert_query, user_data_list)
conn.commit()

print("Data inserted successfully")

cursor.close()
conn.close()
