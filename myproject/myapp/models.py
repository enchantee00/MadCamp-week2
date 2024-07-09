from django.db import models
from django.utils import timezone

class User(models.Model):
    username = models.CharField(max_length=255)
    password = models.CharField(max_length=255)
    role = models.CharField(max_length=255)
    play_count = models.IntegerField(default=0)
    best_score = models.IntegerField(default=0)
    point = models.IntegerField(default=0)
    item_count = models.IntegerField(default=0)
    item_slow_down = models.IntegerField(default=0)
    item_no_bomb = models.IntegerField(default=0)
    item_big_size = models.IntegerField(default=0)
    item_triple_points = models.IntegerField(default=0)
    total_duration = models.DurationField(default=timezone.now)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'users'

class EventTurn(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, db_column='user_id')
    turn_duration = models.DurationField()
    turn_start = models.DateTimeField(default=timezone.now)

    class Meta:
        db_table = 'event_turn'

class EventItemSlowDown(models.Model):
    turn = models.ForeignKey(EventTurn, on_delete=models.CASCADE, db_column='turn_id')
    user = models.ForeignKey(User, on_delete=models.CASCADE, db_column='user_id')
    pressed_ts = models.DurationField(default=timezone.timedelta)  # 변경된 부분
    class Meta:
        db_table = 'event_item_slow_down'

class EventItemNoBomb(models.Model):
    turn = models.ForeignKey(EventTurn, on_delete=models.CASCADE, db_column='turn_id')
    user = models.ForeignKey(User, on_delete=models.CASCADE, db_column='user_id')
    pressed_ts = models.DurationField(default=timezone.timedelta)  # 변경된 부분

    class Meta:
        db_table = 'event_item_no_bomb'

class EventItemBigSize(models.Model):
    turn = models.ForeignKey(EventTurn, on_delete=models.CASCADE, db_column='turn_id')
    user = models.ForeignKey(User, on_delete=models.CASCADE, db_column='user_id')
    pressed_ts = models.DurationField(default=timezone.timedelta)  # 변경된 부분

    class Meta:
        db_table = 'event_item_big_size'

class EventItemTriplePoints(models.Model):
    turn = models.ForeignKey(EventTurn, on_delete=models.CASCADE, db_column='turn_id')
    user = models.ForeignKey(User, on_delete=models.CASCADE, db_column='user_id')
    pressed_ts = models.DurationField(default=timezone.timedelta)  # 변경된 부분

    class Meta:
        db_table = 'event_item_triple_points'