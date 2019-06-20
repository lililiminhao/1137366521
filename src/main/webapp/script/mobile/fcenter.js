(function() {
  var app = new Vue({
    el: "#app",
    data:function() {
      return { 
    	  codeShow: false,
    	  user:null,
    	  codeurl:null
    	};
    },
    methods:{
    	toPerson:function(){
    		location.href = '/jdvop/my/mobile/share_person.php?mobile='+this.user.mobile+'&rankName='+this.user.rankName+'&percent='+this.user.percent;
    	},
    	toWithdraw:function(){
    		location.href = '/jdvop/my/mobile/share_withdraw.php?money='+this.user.money;
    	},
    	toDetail:function(){
    		location.href = '/jdvop/my/mobile/share_money_detail.php?allm='+this.user.money;
    	},
    	share_html:function(){
    		location.href = this.codeurl+'&bitch=1';
    	},
    	getUserInfo:function(){
    		var _self = this;
    		$.ajax({
    			url:'/jdvop/mobile/fenxiao/userCenter.php',
    			dataType:'json',
    			data:{
    				userId:$('#app').attr('userid')
    			},
    			success:function(res){
    				if(res.code == 1){
    					_self.user = res.data;
    					_self.getCode();
    				}
    			}
    		})
    	},
    	getCode:function(){
    		var _self = this;
    		$.ajax({
    			url:'/jdvop/mobile/fenxiao/userQRCode.php',
    			dataType:'json',
    			data:{
    				userId:$('#app').attr('userid')
    			},
    			success:function(res){
    				if(res.code == 1){
    					_self.codeurl = res.data;
    					setTimeout(function(){
    						jQuery(function(){
    							jQuery('.code').qrcode(res.data);
    						})
    						jQuery(function(){
    							jQuery('.c-w').qrcode(res.data);
    						})
    					},200)
    				}
    			}
    		})
    	}
    },
    beforeMount:function(){
    	this.getUserInfo();
    },
    mounted:function() {
      document.getElementsByTagName("html")[0].style.fontSize =
        innerWidth * 0.1 + "px";
      var _self = this;
      setTimeout(function(){
    	  $('.code-wrapper').click(_self.share_html);
      },200)
      
    }
  });
})();
