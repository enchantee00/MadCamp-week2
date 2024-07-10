# myapp/urls.py
from django.urls import path, include
# from rest_framework.routers import DefaultRouter
from .views import LoginView, UserListView, GameItemClickView, GameStartView, GameEndView, GameItemBuyView, Item1PressedView, Item2PressedView, Item3PressedView, Item4PressedView, AppLoginView, QueryView, EventTurnListView, ProfileModifyView, MemoView

# router = DefaultRouter()
# router.register(r'items', ItemViewSet)

urlpatterns = [
    #app
    path('app/login', AppLoginView.as_view(), name='app-login'),
    path('app/turn/item/use', GameItemClickView.as_view(), name='turn-item-click'),
    path('app/turn/start', GameStartView.as_view(), name='turn-start'),
    path('app/turn/end', GameEndView.as_view(), name='turn-end'),
    path('app/item/buy', GameItemBuyView.as_view(), name='item-buy'),
    path('app/profile', ProfileModifyView.as_view(), name='profile-modify'),
    
    #web
    path('login', LoginView.as_view(), name='login'),
    path('item1/pressed', Item1PressedView.as_view(), name='item1-pressed'),
    path('item2/pressed', Item2PressedView.as_view(), name='item2-pressed'),
    path('item3/pressed', Item3PressedView.as_view(), name='item3-pressed'),
    path('item4/pressed', Item4PressedView.as_view(), name='item4-pressed'),
    path('users', UserListView.as_view(), name='user-list'),
    path('query', QueryView().as_view(), name='query-freely'),
    path('turn/start', EventTurnListView.as_view(), name='event-turn-list'),
    path('memos/page/<str:page>', MemoView.as_view(), name='list_memos'),  # GET
    path('memos', MemoView.as_view(), name='create_memo'),  # POST
    path('memos/<int:pk>', MemoView.as_view(), name='modify_memo')  # PUT/PATCH/DELETE
]
