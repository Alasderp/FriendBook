var baseURL="api";
var currentId="";
var map;
var marker;
var marker2;
var markerArray = [];

//the document ready function
try	{
	$(function()
	{
		init();
	}
);} 
catch (e)
{
	alert("*** jQuery not loaded. ***");
}

function init()
{
	map=makeMap("map",1,0.0,0.0);	//make Google map	
	marker=makeMarker(map,0.0,0.0);	//make marker on map, keeping reference	
	marker.setLabel("Change Your Location");
	
	marker2=makeMarker(map,0.0,0.0);
	marker2.setVisible(false);
	
	$("#logIn").click(function()
			{
			getUser();
			}
	);
	
	$("#submitLoc").click(function()
			{
			saveUser(marker.getPosition());
			}
	);
	
	$("#sendRequest").click(function()
			{
			saveRequest();
			}
	);
	
}

function makeMap(divId,zoom,longitude,latitude)
{
	var location=new google.maps.LatLng(latitude,longitude);	//create location from coordinates
	var options={zoom:zoom,		//map options as a map
				center:location,
				mapTypeId:google.maps.MapTypeId.HYBRID};
	var map=new google.maps.Map(document.getElementById(divId),options);	//create map in the given section or div
	return map;	//return map object
	
}

//create a marker on a map
//the marker is returned as we need to get its position later
//
function makeMarker(map,longitude,latitude)
{
var location=new google.maps.LatLng(latitude,longitude);	//create location from coordinates
var marker=new google.maps.Marker({	"position":location,	//mark options as a map
									"map":map,
									"draggable":true});
return marker;	//return marker object
} //end function

function saveUser(position)
{
	var longitude=position.lng();	//get longitude from position of marker
	var latitude=position.lat();		//get latitude from position marker
	var id=$("#idInput").val();	//get user name input text box value
	
	var location=new google.maps.LatLng(latitude,longitude);
	marker2.setPosition(location);
	marker2.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
	marker2.setDraggable(false);
	
	var url=baseURL+"/requestResource/addUser";					//URL of web service
	var data={	"id":id,				//request parameters as a map
			    "latitude":latitude,
				"longitude":longitude
	};

//use jQuery shorthand Ajax POST function
$.post(	url,				//URL of service
		data,			//parameters of request
		function()		//successful callback function
		{
		//alert("User saved: "+id+" ("+longitude+","+latitude+")");
		} //end callback function
	); //end post call
} //end function


function saveRequest()
{

	var subscribeTo=$("#requestInput").val();
	var subscriber = currentId;

	var url=baseURL+"/requestResource/sendRequest";					//URL of web service
	var data={	"subscribeTo":subscribeTo,				//request parameters as a map
			    "subscriber":subscriber
	};

//use jQuery shorthand Ajax POST function
$.post(	url,				//URL of service
		data,			//parameters of request
		function()		//successful callback function
		{

		} //end callback function
	); //end post call
} //end function

function getUser(){
	
	currentId = $("#idInput").val();
	var url=baseURL+"/requestResource/" + currentId;
	var showButton = document.getElementById("submitLoc");
	showButton.style.visibility = "visible";
	
	$.getJSON(url, 
			function(jsonData)
			{
				//longitude=jsonData["longitude"];
				//latitude=jsonData["latitude"];
				//lastUpdated=jsonData["lastUpdated"];
		
				user = jsonData
				
				//alert("id: " + currentId + "Lat, Long" + latitude + ", " + longitude + "Last Updated: " + lastUpdated);
				
				var location=new google.maps.LatLng(user.location.latitude,user.location.longitude);
				marker2.setPosition(location);
				marker2.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
				marker2.setDraggable(false);
				marker2.setVisible(true);
				marker2.setLabel(currentId);
				
			}
	);
	
	loadSubRequests();
	loadSubs();

}

function loadSubRequests(){
	
	var url=baseURL+"/requestResource/requests/all";	
	$.getJSON(url,				//URL of service
		function(requests){	
			$("#requests").empty();	
			for (var i in requests)
				{
				var request=requests[i];
				var subscriber=request["subscriberId"];
				var subscribeTo=request["subscribeTo"];		
				var date=request["timeStamp"];	
				
				if(subscribeTo == currentId){
					
					var d = new Date();
					d.setTime(date)
					var htmlCode="<li id='"+subscriber+"'>Subscriber: "+subscriber+" Date Sent: "+d+"</li>";
					$("#requests").append(htmlCode);
					var btn = document.createElement("BUTTON");        // Create a <button> element
					var t = document.createTextNode("Approve");       // Create a text node
					btn.appendChild(t);
					document.getElementById(subscriber + "").appendChild(btn);
					btn.addEventListener("click", function(){approveSubscriber(currentId, this.parentNode)}, false);
					
					
					btn = document.createElement("BUTTON");        // Create a <button> element
					t = document.createTextNode("Delete");       // Create a text node
					btn.appendChild(t);
					document.getElementById(subscriber + "").appendChild(btn);
					btn.addEventListener("click", function(){denySubscriber(currentId, this.parentNode)}, false);
	
				}
				}
		});
}

function loadSubs(){
	
	var url=baseURL+"/requestResource/subscriptions/all";
	
	for (var i in markerArray){
		markerArray[i].setMap(null)
	}
	markerArray = [];
	
	$("#friendList").empty();	
	
	$.getJSON(url,				//URL of service
			function(subscriptions){	
				for (var i in subscriptions){
					var subscription=subscriptions[i];
					var subscriber=subscription["subscriberId"];
					var subscribeTo=subscription["subscribeTo"];
					if(subscriber == currentId){
						for (var i in subscribeTo){
							var friend = subscribeTo[i]
							var url=baseURL+"/requestResource/" + friend;
							$.getJSON(url, 
									function(jsonData)
									{
										//longitude=jsonData["longitude"];
										//latitude=jsonData["latitude"];
										//lastUpdated=jsonData["lastUpdated"];
								
										user = jsonData
										
										//alert("id: " + currentId + "Lat, Long" + latitude + ", " + longitude + "Last Updated: " + lastUpdated);
										
										var location=new google.maps.LatLng(user.location.latitude,user.location.longitude);
										var tempMarker=makeMarker(map,0.0,0.0);
										tempMarker.setPosition(location);
										tempMarker.setIcon('http://maps.google.com/mapfiles/ms/icons/yellow-dot.png');
										tempMarker.setDraggable(false);
										tempMarker.setVisible(true);
										tempMarker.setLabel(user.id);
								        tempMarker.addListener('click', function() {
								            map.setCenter(tempMarker.getPosition());
								          });
										markerArray.push(tempMarker);
										
										var d = new Date();
										d.setTime(user.lastUpdated)
										var htmlCode="<li id='"+user.id+"'> Friend: "+user.id+" Last Updated: "+d+"</li>";
										$("#friendList").append(htmlCode);
										
									}
							);
						}
					}
				}	
		}
	);
	
}


function approveSubscriber(owner, element){
	var url=baseURL+"/requestResource/approve";
	
	var data={	"subscribeTo":owner,				//request parameters as a map
		    "subscriber":element.id
	};
	
	//use jQuery shorthand Ajax POST function
	$.post(	url,			//URL of service
			data,
			function()		//successful callback function
			{

			} //end callback function
		); //end post call
	
	element.remove();

}



function denySubscriber(owner, element){
	
	var url=baseURL+"/requestResource/deny/"+ owner +"/" + element.id;				//URL pattern of delete service
	var settings={type:"DELETE"};	//options to the $.ajax(...) function call

	$.ajax(url,settings);
	
	element.remove();
}
