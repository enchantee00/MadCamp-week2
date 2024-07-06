# # myapp/models.py

# from django.db import models

# class Item(models.Model):
#     name = models.CharField(max_length=100)
#     description = models.TextField()

#     def __str__(self):
#         return self.name


from django.db import models

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
    total_duration = models.DateTimeField(auto_now_add=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'users'
