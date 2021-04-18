
// var navdata =  eval("(" + window.localStorage.navbar + ")");

	var navdata =  eval("(" + window.sessionStorage.navbar + ")");
	const vm = new Vue({
		el:"#app",
		data:{
			list:navdata,
		},
		methods:{
			
		}
})