# Project bookshelves
If you are publishing this mod or clip of this mod, please attach link to this repo somewhere.

## How to run
Execute commands in this order

If you want to load another video, continue from step 4 (Loading video source)
### 1. Init
```
/theater discord login <Discord bot token>
```

After getting "Logged in successfully" you can proceed with next steps

Also set-up preferably repeating command block, running following command in order to keep displays content updated 
```
/theater display render
```

### 2. Joining bot to your Discord channel
```
/theater discord join <Discord username (Name#1234)>
```

### 3. Displays
You can make as many displays as you want
```
/theater display add <isRGB> <x> <y> <z>
```
To clear all displays run
```
/theater display clear
```

### 4. Loading video source
Run one of the following commands, depending on what you want to do


**If you want to play video** 
```
/theater video load <scale coefficient> <path to video>
```
**If you want screen share** 

Linux: (use coordinates on screen, not in mc)
```
/theater video screenRec linux <scale coefficient> <x> <y> <dx> <dy>
```

Windows:
```
/theater video screenRec windows <scale coefficient> desktop
```

### 5.Playing video
```
/theater video play
```
