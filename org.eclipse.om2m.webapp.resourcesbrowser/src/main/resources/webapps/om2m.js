/*******************************************************************************
 * Copyright (c) 2013-2014 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Thierry Monteil (Project co-founder) - Management and initial specification,
 * 		conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification,
 * 		conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test
 * 		and documentation.
 ******************************************************************************/

/**
* Authors:
* Mahdi Ben Alaya <ben.alaya@laas.fr> <benalaya.mahdi@gmail.com>
* Marouane El kiasse <melkiasse@laas.fr> <kiasmarouane@gmail.com>
* Yassine Banouar <ybanouar@laas.fr> <yassine.banouar@gmail.com>
*/


var sclBase= getUrlVar("sclId");
var context=getUrlVar("context");
$(document).ready(function() {
  $("#main").hide();
});

var username;
var password;
var parser=new DOMParser();

function make_base_auth(user, password) {
  var tok = user + ':' + password;
  var hash = btoa(tok);
  return "Basic " + hash;
}

function login(){
    username = $("input#username").val();
    password = $("input#password").val();
    get(sclBase);
}

function logout(){
    username = "";
    password = "";
    $("input#username").val("")
    $("input#password").val("")
    $("#attributes").html("");
    $('#content').html("");
    $('#response').html("");
    $("#login").show();
    $("#main").hide();
}

function get(targetId){
    $.ajax({
         type: "GET",
         url: context+"/"+targetId,
         headers: {"Authorization":make_base_auth(username,password)},
         beforeSend: function() {},
         timeout: 20000,
         error: function(xhr, status, error) {
             $("#error").html(xhr.status+' '+status+' '+error);
         },
         dataType: 'xml',
         success: function(response){
             $("#login").hide();
             $("#main").show();
             $("#url").text(window.location.protocol+"//"+window.location.host+context+"/"+targetId);
             $("#"+encodeId(targetId)).html("");
             $("#attributes").html("");
             $('#content').html('');
             $('#response').html('');
             $("#error").html('');

             if(response.firstChild.localName=="sclBase"){
                 $("#resources").html("<li onclick=get('"+targetId+"')>"+targetId+"<ul id="+targetId+"></ul></li>");
             }
             var childrens = $(response.firstChild).children();

             for(var i=0; i<childrens.length; i++){
                 if(childrens[i].localName.indexOf("Reference") >= 0){
                     $("#"+encodeId(targetId)).append("<li onclick=get('"+childrens[i].textContent+"')>"+childrens[i].localName.split("Reference")[0]+"<ul id="+encodeId(childrens[i].textContent)+"></ul></li>");
                 }
                 else if(childrens[i].localName.indexOf("Collection") >= 0){
                     $("#"+encodeId(targetId)).append("<li onclick=get('"+targetId+"')>"+childrens[i].localName+"<ul id="+encodeId(targetId+"_"+childrens[i].localName)+"></ul></li>");
                     var collection = $(childrens[i]).children();
                     if(childrens[i].localName.indexOf("contentInstanceCollection") >= 0){
                         for(var j=0; j<collection.length; j++){
                             $("#"+encodeId(targetId+"_"+childrens[i].localName)).append("<li onclick=get('"+$(collection[j]).attr("href")+"')>"+$(collection[j]).attr("om2m:id")+"<ul id="+encodeId($(collection[j]).attr("href"))+"></ul></li>");
                         }
                     }else{
                         for(var j=0; j<collection.length; j++){
                             $("#"+encodeId(targetId+"_"+childrens[i].localName)).append("<li onclick=get('"+collection[j].textContent+"')>"+$(collection[j]).attr("id")+"<ul id="+encodeId(collection[j].textContent)+"></ul></li>");
                         }
                     }
                 }

                 else{
                     if(childrens[i].localName=="content"){
                    	    console.log("Call display "+i);

                         display(childrens[i]);
                     }else if(childrens[i].localName=="link"){
                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td> <input type='button' onclick=get('"+childrens[i].textContent+"') value="+childrens[i].textContent+" /></td></tr>");
                     }else if(childrens[i].localName=="permissions" || childrens[i].localName=="selfPermissions"){
                         $("#attributes").append("<tr><td>"+childrens[i].localName+"</td><td><table class='bordered' id="+childrens[i].localName+"><thead><th>permission</th><th>flags & holders</th></thead></table></td></tr>");
                         var permissions = $(childrens[i]).children();
                         for(var j=0; j<permissions.length; j++){
                             var flags;
                             var holders;
                             var permChildren = $(permissions[j]).children();
                             for(var k=0; k<permChildren.length;k++){
                                 if(permChildren[k].localName=="permissionFlags"){
                                     flags=$(permChildren[k]).children();
                                 }else if(permChildren[k].localName=="permissionHolders"){
                                     holders=$(permChildren[k]).children();
                                 }
                             }

                             $("#"+encodeId(childrens[i].localName)).append("<tr><td>"+$(permissions[j]).attr("om2m:id")+
                                     "</td><td><table class='bordered' ><thead><th colspan="+flags.length+">flags</th></thead><tr id="+encodeId($(permissions[j]).attr("om2m:id")+"_flags")+" ></tr></table>" +
                                     "<table class='bordered' id="+encodeId($(permissions[j]).attr("om2m:id")+"_holders") +"><thead><th>holders</th></thead></table></td></tr>");

                             for(var k=0; k<flags.length; k++){
                                 $("#"+encodeId($(permissions[j]).attr("om2m:id")+"_flags")).append("<td>"+flags[k].textContent+"</td>");
                             }

                             for(var k=0; k<holders.length; k++){
                                 $("#"+encodeId($(permissions[j]).attr("om2m:id")+"_holders")).append("<tr><td>"+holders[k].textContent+"</td></tr>");
                             }
                         }
                     }else if(childrens[i].localName=="members" || childrens[i].localName=="discoveryURI"){

                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td><table class='bordered' id="+childrens[i].localName+" ><thead><th>URI</th></thead></table></td></tr>");
                         var uris =$(childrens[i]).children();
                         for(var k=0; k<uris.length; k++){
                             $("#"+encodeId(childrens[i].localName)).append("<tr><td>"+uris[k].textContent+"</td></tr>");
                         }
                     }else if(childrens[i].localName=="searchStrings"){
                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td><table class='bordered' id="+childrens[i].localName+" ><thead><th>searchString</th></thead></table></td></tr>");
                         var strings =$(childrens[i]).children();
                         for(var k=0; k<strings.length; k++){
                             $("#"+encodeId(childrens[i].localName)).append("<tr><td>"+strings[k].textContent+"</td></tr>");
                         }
                     }else if(childrens[i].localName=="announceTo"){
                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td><table class='bordered' id="+childrens[i].localName+" ><thead><th>name</th><th>value</th></thead></table></td></tr>");
                         var announceParams =$(childrens[i]).children();
                         for(var k=0; k<announceParams.length; k++){
                             $("#"+encodeId(childrens[i].localName)).append("<tr><td>"+announceParams[k].localName+"</td><td>"+announceParams[k].textContent+"</td></tr>");
                         }
                     }else if(childrens[i].localName=="aPoCPaths"){
                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td><table class='bordered' id="+childrens[i].localName+" ><thead><th>path</th><th>accessRightID</th><th>searchStrings</th></thead></table></td></tr>");
                         var apocpaths = $(childrens[i]).children();
                         console.log(apocpaths);

                         for(var k=0; k<apocpaths.length; k++){
                             pathchildren = $(apocpaths[k]).children();
                             var path;
                             var pathAccessRight;
                             var pathSearchStrings="";
                             for(var l=0; l<pathchildren.length; l++){
                                 if(pathchildren[l].localName=="path"){
                                     path=pathchildren[l].textContent;
                                 }else if(pathchildren[l].localName=="accessRightID"){
                                     pathAccessRight=pathchildren[l].textContent;
                                 }else if(pathchildren[l].localName=="searchStrings"){
                                     var pathSearchChildren = $(pathchildren[l]).children();
                                     for(var j=0; j<pathSearchChildren.length; j++){
                                         pathSearchStrings= pathSearchStrings+pathSearchChildren[j].textContent+"<br/>";
                                     }
                                 }
                             }
                             $("#"+encodeId(childrens[i].localName)).append("<tr><td>"+path+"</td><td>"+pathAccessRight+"</td><td>"+ pathSearchStrings+"</td></tr>");

                         }
                     }else{

                         $("#attributes").append("<tr><td class="+childrens[i].localName+">"+childrens[i].localName+"</td><td>"+childrens[i].textContent+"</td></tr>");
                     }
                 }
             }
             $("li").click(function (e) {
                 e.stopPropagation();
             });
         }
    });
}

function encodeId(id){
    return id.replace(/[\n\s]/g,'').replace(/[\/]/gi,"_");
}

function clean(text){
    return text.replace(/[\n\s]/g,'');
}

function display(content){
    $("#attributes").append("<tr><td class="+content.localName+">"+content.localName+"</td><td id='cont'></td></tr>");

    $('#cont').append("<table class='bordered' id='contentTable'><thead><tr><th>Attribute</th><th>Value</th></tr></thead></table>");

    var obix= atob(content.textContent);
    var rep=parser.parseFromString(obix,'text/xml');


    if(rep.firstChild.tagName!="obj"){
        $('#contentTable').append('<tr><td>'+$(rep.firstChild).attr('name')+'</td><td>'+$(rep.firstChild).attr('val')+'</td></tr>');
    }

    var childrens =$(rep.firstChild).children();

    for(var t=0; t<childrens.length; t++){
         if(childrens[t].tagName=="op"){
             if($(childrens[t]).attr('is')=="retrieve"){
                 $('#contentTable').append("<tr><td><input type='button' onclick=retrieve('"+$(childrens[t]).attr('href')+"') value='"+$(childrens[t]).attr('name')+"' ></td><td>"+$(childrens[t]).attr('href')+"</td></tr>");
             }else if ($(childrens[t]).attr('is')=="execute"){
                 $('#contentTable').append("<tr><td><input type='button' onclick=execute('"+$(childrens[t]).attr('href')+"') value='"+$(childrens[t]).attr('name')+"' ></td><td>"+$(childrens[t]).attr('href')+"</td></tr>");
             }else if ($(childrens[t]).attr('is')=="create"){
                 var objs = childrens;
                 for(var j=0; j<objs.length; j++){
                     if(objs[j].tagName=="obj"){
                         if($(objs[j]).attr('href')==$(childrens[t]).attr('in')){
                             var serializer = new XMLSerializer();
                             content = serializer.serializeToString( objs[j] );
                             $('#contentTable').append("<tr><td><input type='button' onclick=create('"+$(childrens[t]).attr('href')+"','"+btoa(content)+"') value='"+$(childrens[t]).attr('name')+"' ></td><td>"+$(childrens[t]).attr('href')+"</td></tr>");
                             break;
                         }
                     }
                 }
             }
         }
         else if(childrens[t].tagName!="obj"){
             $('#contentTable').append('<tr><td>'+$(childrens[t]).attr('name')+'</td><td>'+$(childrens[t]).attr('val')+'</td></tr>');
         }
    }
}

function getUrlVar(key){
  var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
  return result && unescape(result[1]) || "";
}

function execute(url){
  $('#response').html('');
  $.ajax({
    type: 'POST',
    url: context+'/'+url,
    headers: {"Authorization":make_base_auth(username,password)},
    beforeSend: function() {
    },
    timeout: 20000,
    error: function(xhr, status, error) {
      if(xhr.status==204) successCallback(null, error, xhr);
      else  $('#response').append('<h4>Post request failed: '+xhr.status+' '+error+'</h4>'); },
    dataType: 'xml',
    success: function(response) {
      $('#response').append('<h4>Successful POST request.</h4>');
    }
  });
}

function retrieve(url){
  $('#response').html('');
  $.ajax({
    type: 'GET',
    url: context+'/'+url,
    headers: {"Authorization":make_base_auth(username,password)},
    beforeSend: function() {
    },
    timeout: 20000,
    error: function(xhr, status, error) { $('#response').append('<h4>GET request failed: '+xhr.status+' '+error+'</h4>'); },     // alert a message in case of error
    dataType: 'xml',
    success: function(response) {

      $('#response').append('<h4>Successful GET Request:</h4>');
      $('#response').append('<table class="bordered" id="contentTable1" ><thead><tr><th >Name</th><th >Value</th></thead></table>');

      if(response.firstChild.localName=="obj"){
	      $(response).find('obj').children().each(function() {
	        $('#contentTable1').append('<tr"><td>'+$(this).attr('name')+'</td><td>'+$(this).attr('val')+'</td></tr>');
	      });
      }else{
    	  $(response).children().each(function() {
  	        $('#contentTable1').append('<tr"><td>'+$(this).attr('name')+'</td><td>'+$(this).attr('val')+'</td></tr>');
  	      });
      }
    }
  });
}
  function create(url, content){
      $('#response').html('');
      $.ajax({
        type: 'POST',
        url: context+'/'+url,
        headers: {"Authorization":make_base_auth(username,password)},
        beforeSend: function() {
        },
        timeout: 20000,
        data: atob(content),
        error: function(xhr, status, error) {
          if(xhr.status==204) successCallback(null, error, xhr);
          else  $('#response').append('<h4>Post request failed: '+xhr.status+' '+error+'</h4>');
          },
        dataType: 'xml',
        success: function(response) {
          $('#response').append('<h4>Successful POST request.</h4>');
        }
      });
}
