
				$(function() {
				
					$('#da-thumbs > li').hoverdir( {
						//hoverDelay	: 75
					} );

				});

		//******************轮播图******************************
			$(function(){
				//代码初始化
				var size=$('.banner-list li').size();
				for (var i = 2; i <= size; i++) {
					var li="<li></li>";
					$('.slider').append(li);
				};
				//手动控制轮播图
				$('.banner-list li').eq(0).show();
				$('.slider li').eq(0).addClass('active');
				$('.slider li').mouseover(function(){
					$(this).addClass('active').siblings().removeClass('active');
					var index=$(this).index();
					i=index;
					$('.banner-list li').eq(index).stop().fadeIn(1000).siblings().stop().fadeOut(1000);
				})
				//自动播放
				var i=0;
				var t=setInterval(move,2000);
				//自动播放核心函数
				function move(){
					i++;
					if(i==size){
						i=0;
					}
					$('.slider li').eq(i).addClass('active').siblings().removeClass('active');
					$('.banner-list li').eq(i).fadeIn(1000).siblings().fadeOut(1000);
				}

				//鼠标移入移除
				$('.u-banner').hover(function(){
					clearInterval(t);
				},function(){
					t=setInterval(move,1500);
				})
			})
//******************轮播图结束******************************
//当滚动条的位置处于距顶部100像素以下时，显示侧边栏  
        $(function () {  
            $(window).scroll(function(){  
                if ($(window).scrollTop()>100){  
                    $("#float_bar").fadeIn(1500);  
                }  
                else  
                {  
                    $("#float_bar").fadeOut(1500);  
                }  
            });  
  
            //当点击跳转链接后，回到页面顶部位置  
  
            $("#to_top").click(function(){  
                $('body,html').animate({scrollTop:0},1000);  
                return false;  
            });  
        });  