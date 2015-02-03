![alt text](http://i.imgur.com/k219avn.png "GalleryMirror Logo")

GalleryMirror is a imgur mirroring program. The purpose is to allow you to keep an always updated copy of a imgur gallery on your harddrive. This can be useful if you're someone who loves a particular type of image commonly found on imgur. Tag Galleries enable GalleryMirror to download every image by a tag.

== Config == 
> {"clientId":"<your_client_id>","delay":30,"gallery":"reaction_gifs"}
> client_id is your imgur api client id
> delay is in minutes, how often to check for gallery updates
> gallery is the tag you want to download

== Running ==
> java -jar GalleryMirror.jar

You will get a response code error because invalid client id, so replace your client id in the newly generated config and run the command again
