# Generated by Django 5.0.6 on 2024-07-07 16:47

import django.db.models.deletion
import django.utils.timezone
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('username', models.CharField(max_length=255)),
                ('password', models.CharField(max_length=255)),
                ('role', models.CharField(max_length=255)),
                ('play_count', models.IntegerField(default=0)),
                ('best_score', models.IntegerField(default=0)),
                ('point', models.IntegerField(default=0)),
                ('item_count', models.IntegerField(default=0)),
                ('item_slow_down', models.IntegerField(default=0)),
                ('item_no_bomb', models.IntegerField(default=0)),
                ('item_big_size', models.IntegerField(default=0)),
                ('item_triple_points', models.IntegerField(default=0)),
                ('total_duration', models.DurationField(default=django.utils.timezone.now)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'db_table': 'users',
            },
        ),
        migrations.CreateModel(
            name='EventTurn',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('turn_duration', models.DurationField()),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.user')),
            ],
            options={
                'db_table': 'event_turn',
            },
        ),
        migrations.CreateModel(
            name='EventItemTriplePoints',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pressed_ts', models.DateTimeField()),
                ('turn', models.ForeignKey(db_column='turn_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.eventturn')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.user')),
            ],
            options={
                'db_table': 'event_item_triple_points',
            },
        ),
        migrations.CreateModel(
            name='EventItemSlowDown',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pressed_ts', models.DateTimeField()),
                ('turn', models.ForeignKey(db_column='turn_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.eventturn')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.user')),
            ],
            options={
                'db_table': 'event_item_slow_down',
            },
        ),
        migrations.CreateModel(
            name='EventItemNoBomb',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pressed_ts', models.DateTimeField()),
                ('turn', models.ForeignKey(db_column='turn_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.eventturn')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.user')),
            ],
            options={
                'db_table': 'event_item_no_bomb',
            },
        ),
        migrations.CreateModel(
            name='EventItemBigSize',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pressed_ts', models.DateTimeField()),
                ('turn', models.ForeignKey(db_column='turn_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.eventturn')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, to='myapp.user')),
            ],
            options={
                'db_table': 'event_item_big_size',
            },
        ),
    ]
