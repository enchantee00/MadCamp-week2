# myapp/urls.py
from django.urls import path, include
# from rest_framework.routers import DefaultRouter
from .views import LoginView, UserListView

# router = DefaultRouter()
# router.register(r'items', ItemViewSet)

urlpatterns = [
    path('login/', LoginView.as_view(), name='login'),
    path('users/', UserListView.as_view(), name='user-list'),
    # path('', include(router.urls))
]
