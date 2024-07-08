from django.contrib.auth.hashers import check_password
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User, EventTurn, EventItemSlowDown, EventItemBigSize, EventItemNoBomb, EventItemTriplePoints
from .serializers import UserSerializer, EventItemBigSizeSerializer, EventItemNoBombSerializer, EventItemSlowDownSerializer, EventItemTriplePointsSerializer, EventTurnSerializer
from datetime import timedelta

def milliseconds_to_timedelta(milliseconds):
    return timedelta(milliseconds=milliseconds)

#Web
class LoginView(APIView):
    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')
        try:
            user = User.objects.get(username=username)
            if check_password(password, user.password):
                return Response({"status": "success", "user_id": user.id})
            else:
                return Response({"status": "fail", "message": "Incorrect password."}, status=status.HTTP_401_UNAUTHORIZED)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User does not exist."}, status=status.HTTP_401_UNAUTHORIZED)

# 예를 들어, User 목록을 가져오는 뷰를 추가할 수 있습니다.
class UserListView(APIView):
    def get(self, request):
        users = User.objects.all()
        serializer = UserSerializer(users, many=True)
        return Response(serializer.data)

## App
# class AppLoginView(APIView):
#     def post(self, request):
#         username = request.data.get('username')
#         password = request.data.get('password')
#         try:
#             user = User.objects.get(username=username)
#             if check_password(password, user.password):
#                 return Response({"status": "success", "user_id": user.id})
#             else:
#                 return Response({"status": "fail", "message": "Incorrect password."}, status=status.HTTP_401_UNAUTHORIZED)
#         except User.DoesNotExist:
#             return Response({"status": "fail", "message": "User does not exist."}, status=status.HTTP_401_UNAUTHORIZED)

class GameItemClickView(APIView): #각 Item click 시
    def post(self, request):
        user_id = request.data.get('user_id')
        turn_id = request.data.get('turn_id')
        pressed_ts = request.data.get('pressed_ts')
        item_id = request.data.get('item_id')

        if not all([user_id, turn_id, pressed_ts, item_id]):
            return Response({"status": "fail", "message": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)

        # Verify the existence of the user and turn
        try:
            user = User.objects.get(pk=user_id)
            turn = EventTurn.objects.get(pk=turn_id)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found."}, status=status.HTTP_404_NOT_FOUND)
        except EventTurn.DoesNotExist:
            return Response({"status": "fail", "message": "Turn not found."}, status=status.HTTP_404_NOT_FOUND)

        # Insert into the appropriate table based on item_id
        if item_id == 1:
            EventItemSlowDown.objects.create(user=user, turn=turn, pressed_ts=pressed_ts)
        elif item_id == 2:
            EventItemNoBomb.objects.create(user=user, turn=turn, pressed_ts=pressed_ts)
        elif item_id == 3:
            EventItemBigSize.objects.create(user=user, turn=turn, pressed_ts=pressed_ts)
        elif item_id == 4:
            EventItemTriplePoints.objects.create(user=user, turn=turn, pressed_ts=pressed_ts)
        else:
            return Response({"status": "fail", "message": "Invalid item_id."}, status=status.HTTP_400_BAD_REQUEST)

        return Response({"status": "success"}, status=status.HTTP_201_CREATED)
    
class GameStartView(APIView): #게임 시작 시
    def post(self, request):
        user_id = request.data.get('user_id')
        turn_duration = milliseconds_to_timedelta(int(request.data.get('turn_duration')))

        if not all([user_id, turn_duration]):
            return Response({"status": "fail", "message": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found."}, status=status.HTTP_404_NOT_FOUND)
        
        
        turn = EventTurn.objects.create(user=user, turn_duration=turn_duration)
        return Response({"status": "success", "turn_id": turn.pk}, status=status.HTTP_201_CREATED)
    
class GameEndView(APIView): #게임 끝낼 시
    def patch(self, request):
        user_id = request.data.get('user_id')
        turn_id = request.data.get('turn_id')
        turn_duration = milliseconds_to_timedelta(int(request.data.get('turn_duration')))
        item_slow_down_used = request.data.get('item_slow_down_used')
        item_no_bomb_used = request.data.get('item_no_bomb_used')
        item_big_size_used = request.data.get('item_big_size_used')
        item_triple_points_used = request.data.get('item_triple_points_used')
        score = request.data.get('score')
        
        if any(x is None for x in [user_id, turn_id, turn_duration, item_slow_down_used, item_no_bomb_used, item_big_size_used, item_triple_points_used, score]):
            return Response({"status": "fail", "message": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            turn = EventTurn.objects.get(pk=turn_id)
        except EventTurn.DoesNotExist:
            return Response({"status": "fail", "message": "Turn not found."}, status=status.HTTP_404_NOT_FOUND)
        
        try:
            user = User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found."}, status=status.HTTP_404_NOT_FOUND)
        
        
        # 시리얼라이저를 통한 User 업데이트
        total_item_used = item_slow_down_used + item_no_bomb_used + item_big_size_used + item_triple_points_used
        user_data = {
            'play_count': user.play_count + 1,
            'best_score': score if user.best_score < score else user.best_score,
            'item_count': user.item_count - total_item_used,
            'total_duration': user.total_duration + turn_duration,
            'item_slow_down': user.item_slow_down - int(item_slow_down_used),
            'item_no_bomb': user.item_no_bomb - int(item_no_bomb_used),
            'item_big_size': user.item_big_size - int(item_big_size_used),
            'item_triple_points': user.item_triple_points - int(item_triple_points_used)
        }
        user_serializer = UserSerializer(user, data=user_data, partial=True)
        if user_serializer.is_valid():
            user_serializer.save()
        else:
            return Response({"status": "fail", "errors": user_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)

        # 시리얼라이저를 통한 EventTurn 부분 업데이트
        turn_data = {'turn_duration': turn_duration}
        turn_serializer = EventTurnSerializer(turn, data=turn_data, partial=True)
        if turn_serializer.is_valid():
            turn_serializer.save()
        else:
            return Response({"status": "fail", "errors": turn_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)

        return Response({
            "status": "success",
            "user_data": user_serializer.data,
            "turn_data": turn_serializer.data
        }, status=status.HTTP_200_OK)

    
    
class GameItemBuyView(APIView): #아이템 구입 시
    def patch(self, request):
        user_id = request.data.get('user_id')
        item_id = request.data.get('item_id')
        cost = request.data.get('cost')
        count = request.data.get('count')
        
        if any(x is None for x in [user_id, item_id, cost, count]):
            return Response({"status": "fail", "message": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)
        
        try:
            user = User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found."}, status=status.HTTP_404_NOT_FOUND)
        
        
        # 아이템 ID와 필드를 매핑
        item_fields = {
            1: 'item_slow_down',
            2: 'item_no_bomb',
            3: 'item_big_size',
            4: 'item_triple_points',
        }
        # 아이템 ID에 따라 필드 업데이트
        if item_id in item_fields:
            item_field = item_fields[item_id]
            new_item_count = getattr(user, item_field) + count  # 기존 값에 추가
            
            # 시리얼라이저를 통한 User 업데이트
            user_data = {
                'item_count': user.item_count + count,
                'point': user.point - int(cost),
                item_field: new_item_count  # 해당 아이템 필드 업데이트
            }
            user_serializer = UserSerializer(user, data=user_data, partial=True)
            if user_serializer.is_valid():
                user_serializer.save()
            else:
                return Response({"status": "fail", "errors": user_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)

            return Response({
                "status": "success",
                "user_data": user_serializer.data
            }, status=status.HTTP_200_OK)
        else:
            return Response({"status": "fail", "message": "Invalid item ID."}, status=status.HTTP_400_BAD_REQUEST)

        

        


        
