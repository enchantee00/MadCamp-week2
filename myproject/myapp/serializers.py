from rest_framework import serializers
from .models import User, EventTurn, EventItemSlowDown, EventItemNoBomb, EventItemBigSize, EventItemTriplePoints, Memo

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'

class EventTurnSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventTurn
        fields = '__all__'

class EventItemSlowDownSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventItemSlowDown
        fields = '__all__'

class EventItemNoBombSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventItemNoBomb
        fields = '__all__'

class EventItemBigSizeSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventItemBigSize
        fields = '__all__'

class EventItemTriplePointsSerializer(serializers.ModelSerializer):
    class Meta:
        model = EventItemTriplePoints
        fields = '__all__'

class MemoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Memo
        fields = '__all__'