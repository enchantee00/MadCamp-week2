from django.contrib.auth.hashers import check_password
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User, EventTurn, EventItemSlowDown, EventItemBigSize, EventItemNoBomb, EventItemTriplePoints, Memo
from .serializers import UserSerializer, EventItemBigSizeSerializer, EventItemNoBombSerializer, EventItemSlowDownSerializer, EventItemTriplePointsSerializer, EventTurnSerializer, MemoSerializer
from datetime import timedelta
import pymysql
from django.conf import settings

def milliseconds_to_timedelta(milliseconds):
    return timedelta(milliseconds=milliseconds)


class UserListView(APIView):
    def get(self, request):
        users = User.objects.all()
        serializer = UserSerializer(users, many=True)
        return Response(serializer.data)

class AppLoginView(APIView):
    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')
        try:
            user = User.objects.get(role='player', username=username)
            if check_password(password, user.password):
                response_data = {
                    "username": user.username,
                    "best_score": user.best_score,
                    "point": user.point,
                    "item_slow_down": user.item_slow_down,
                    "item_no_bomb": user.item_no_bomb,
                    "item_triple_points": user.item_triple_points,
                    "item_big_size": user.item_big_size
                }
                return Response({"status": "success", "user_id": user.id, **response_data}, status=status.HTTP_200_OK)
            else:
                return Response({"status": "fail", "message": "Invalid password"}, status=status.HTTP_401_UNAUTHORIZED)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found"}, status=status.HTTP_404_NOT_FOUND)
        
        
class GameItemClickView(APIView): #각 Item click 시
    def post(self, request):
        user_id = request.data.get('user_id')
        turn_id = request.data.get('turn_id')
        pressed_ts = milliseconds_to_timedelta(int(request.data.get('pressed_ts')))
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
        turn_start = request.data.get('turn_start')

        if not all([user_id, turn_duration, turn_start]):
            return Response({"status": "fail", "message": "All fields are required."}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User not found."}, status=status.HTTP_404_NOT_FOUND)
        
        
        turn = EventTurn.objects.create(user=user, turn_duration=turn_duration, turn_start=turn_start)
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
            'item_count': total_item_used,
            'total_duration': user.total_duration + turn_duration,
            'item_slow_down': int(item_slow_down_used),
            'item_no_bomb': int(item_no_bomb_used),
            'item_big_size': int(item_big_size_used),
            'item_triple_points': int(item_triple_points_used),
            'point': user.point + score
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


class ProfileModifyView(APIView):
    def patch(self, request):
        user_id = request.data.get('user_id')
        name = request.data.get('name')
        
        if not user_id or not name:
            return Response({"status": "fail", "message": "User ID and name are required."}, status=status.HTTP_400_BAD_REQUEST)
        
        user = User.objects.get(id=user_id)
        user.username = name
        user.save()
        
        serializer = UserSerializer(user)
        
        return Response({"status": "success", "data": serializer.data}, status=status.HTTP_200_OK)

    
#Web
class LoginView(APIView):
    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')
        try:
            user = User.objects.get(role='admin', username=username)
            if check_password(password, user.password):
                return Response({"status": "success", "user_id": user.id})
            else:
                return Response({"status": "fail", "message": "Incorrect password."}, status=status.HTTP_401_UNAUTHORIZED)
        except User.DoesNotExist:
            return Response({"status": "fail", "message": "User does not exist."}, status=status.HTTP_401_UNAUTHORIZED)
        
class Item1PressedView(APIView):
    def get(self, request):
        item_slow_down = EventItemSlowDown.objects.all()
        serializer = EventItemSlowDownSerializer(item_slow_down, many=True)
        return Response(serializer.data)

class Item2PressedView(APIView):
    def get(self, request):
        item_no_bomb = EventItemNoBomb.objects.all()
        serializer = EventItemNoBombSerializer(item_no_bomb, many=True)
        return Response(serializer.data)

class Item3PressedView(APIView):
    def get(self, request):
        item_big_size = EventItemBigSize.objects.all()
        serializer = EventItemBigSizeSerializer(item_big_size, many=True)
        return Response(serializer.data)
    
class Item4PressedView(APIView):
    def get(self, request):
        item_triple_points = EventItemTriplePoints.objects.all()
        serializer = EventItemTriplePointsSerializer(item_triple_points, many=True)
        return Response(serializer.data)


class QueryView(APIView):
    def post(self, request):
        query = request.data.get('query')

        if not query:
            return Response({"error": "Query is required"}, status=status.HTTP_400_BAD_REQUEST)

        # 데이터베이스 연결 설정
        db_settings = settings.DATABASES['default']
        connection = pymysql.connect(
            host=db_settings['HOST'],
            user=db_settings['USER'],
            password=db_settings['PASSWORD'],
            db=db_settings['NAME'],
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )

        try:
            with connection.cursor() as cursor:
                cursor.execute(query)
                result = cursor.fetchall()
            connection.commit()
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        finally:
            connection.close()

        return Response({"result": result}, status=status.HTTP_200_OK)
        
        
class EventTurnListView(APIView):
    def get(self, request):
        turns = EventTurn.objects.all()
        serializer = EventTurnSerializer(turns, many=True)
        return Response(serializer.data)
        

class MemoView(APIView):
    def get(self, request, page=None):
        if page:
            memos = Memo.objects.filter(page=page)
            serializer = MemoSerializer(memos, many=True)
            return Response(serializer.data)
        return Response({"error": "Page not specified"}, status=status.HTTP_400_BAD_REQUEST)

    def post(self, request):
        serializer = MemoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def put(self, request, pk):
        try:
            memo = Memo.objects.get(pk=pk)
        except Memo.DoesNotExist:
            return Response({"error": "Memo not found"}, status=status.HTTP_404_NOT_FOUND)

        serializer = MemoSerializer(memo, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def patch(self, request, pk):
        try:
            memo = Memo.objects.get(pk=pk)
        except Memo.DoesNotExist:
            return Response({"error": "Memo not found"}, status=status.HTTP_404_NOT_FOUND)

        serializer = MemoSerializer(memo, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk):
        try:
            memo = Memo.objects.get(pk=pk)
            memo.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except Memo.DoesNotExist:
            return Response({"error": "Memo not found"}, status=status.HTTP_404_NOT_FOUND)