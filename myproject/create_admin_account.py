import os
import django
from django.utils import timezone
from django.contrib.auth.hashers import make_password
from datetime import timedelta

# Django 프로젝트의 설정 모듈 설정
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from myapp.models import User

# 원하는 비밀번호
password = 'password'

# 비밀번호 해시 생성
hashed_password = make_password(password)

# 관리자 계정 생성
admin_user = User.objects.create(
    username='daemo',
    password=hashed_password,
    role='player',
    play_count=12,
    best_score=1385,
    point=5000,
    item_count=12,
    item_slow_down=3,
    item_no_bomb=3,
    item_big_size=3,
    item_triple_points=3,
    total_duration= timedelta(20000),
    created_at=timezone.now()
)

admin_user.save()

print("Admin user created successfully!")
