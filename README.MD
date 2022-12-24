# Project bookshelves

## How to run
Execute commands in this order

If you want to load another video, continue from step 2 (Joining bot to your Discord channel), DO NOT RUN INIT AGAIN
### 1. Init
```
/theater login <Discord bot token>
```

After getting "Logged in successfully" you can proceed with next steps

Also set-up preferably repeating command block, running following command in order to keep displays content updated 
```
/theater render
```

### 2. Joining bot to your Discord channel
```
/theater join <Discord TAG>
```

### 3. Loading video source
Run one of the following commands, depending on what you want to do


**If you want to play video** 
```
/theater load <scale coefficient> <path to video>
```
**If you want screen share** 

Linux: (use coordinates on screen, not in mc)
```
/theater linScreenRec <scale coefficient> <x> <y> <dx> <dy>
```

Windows:
```
/theater winScreenRec <scale coefficient> desktop
```

### 4. Displays
You can make as many displays as you want
```
/theater display <x> <y> <z>
```
