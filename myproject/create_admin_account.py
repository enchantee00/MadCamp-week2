import os
import django
from django.utils import timezone
from django.contrib.auth.hashers import make_password

# Django 프로젝트의 설정 모듈 설정
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from myapp.models import User

# 원하는 비밀번호
password = 'youwillneverthinkthisispassword'

# 비밀번호 해시 생성
hashed_password = make_password(password)

# 관리자 계정 생성
admin_user = User.objects.create(
    username='yigyeom',
    password=hashed_password,
    role='admin',
    play_count=-1,
    best_score=-1,
    point=-1,
    item_count=-1,
    item_slow_down=-1,
    item_no_bomb=-1,
    item_big_size=-1,
    item_triple_points=-1,
    total_duration=timezone.now(),
    created_at=timezone.now()
)

admin_user.save()

print("Admin user created successfully!")
