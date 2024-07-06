from django.contrib.auth.hashers import check_password
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User
from .serializers import UserSerializer

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
