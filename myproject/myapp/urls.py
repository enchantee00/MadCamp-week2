# myapp/urls.py
from django.urls import path, include
# from rest_framework.routers import DefaultRouter
from .views import LoginView, UserListView, GameItemClickView, GameStartView, GameEndView, GameItemBuyView

# router = DefaultRouter()
# router.register(r'items', ItemViewSet)

urlpatterns = [
    path('login', LoginView.as_view(), name='login'),
    path('users', UserListView.as_view(), name='user-list'),
    # path('app/login/', AppLoginView.as_view(), name='app-login')
    path('app/turn/item/use', GameItemClickView.as_view(), name='turn-item-click'),
    path('app/turn/start', GameStartView.as_view(), name='turn-start'),
    path('app/turn/end', GameEndView.as_view(), name='turn-end'),
    path('app/item/buy', GameItemBuyView.as_view(), name='item-buy')
]
