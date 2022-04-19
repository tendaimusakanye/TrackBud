# TrackBud
TrackBud is a full-fledged music player for Android (Mobile ,TV, Wear) and is currently under development.

P.S. I am still working of finding a better name 🤣

Server side logic is complete.
Client side logic ( UI for TV, Wear, Mobile ) is half done. 
## Features
* Local Audio Playback
* Eleagant UI 🤣

## Architecture
TrackBud follows a client - server architecture as described in the android docs. 
[Building an audio app](https://developer.android.com/guide/topics/media-apps/audio-app/building-an-audio-app). It is written entirely in Kotlin
### Server Architecture
#### MusicService
[MusicService](https://github.com/tendaimusakanye/TrackBud/blob/master/common/src/main/java/com/tendai/common/MusicService.kt)
which is a subclass of 
[MediaBrowserService](https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowser-client.html)
is a very important
class. It allows
[MediaBrowser](https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowser-client.html) 
clients and other applications to discover the music service, connect to the media session and control playback. 

Music Service is also responsible for:
* the audio player in this case [MediaPlayer](https://developer.android.com/guide/topics/media/mediaplayer)
* the [media session](https://developer.android.com/guide/topics/media-apps/working-with-a-media-session) and the objects that communicate with it
* maintaining a [notification](https://developer.android.com/guide/topics/ui/notifiers/notifications) which displays audio information and some controls
* loading audio files from local storage

#### Loading audio files
To retrieve audio files the application uses the repository pattern. See [Android Architecture Blueprints v1](https://github.com/android/architecture-samples/tree/todo-mvp-clean)
. Audio files are loaded using a [ContentProvider](https://developer.android.com/guide/topics/providers/content-provider-basics).

#### Audio Playback
The [PlaybackManager](https://github.com/tendaimusakanye/TrackBud/blob/master/common/src/main/java/com/tendai/common/playback/PlaybackManager.kt) class manages playing the actual audio files based on 
[media session callbacks](https://developer.android.com/guide/topics/media-apps/audio-app/mediasession-callbacks). 
It communicates with the [QueueManager](https://github.com/tendaimusakanye/TrackBud/blob/master/common/src/main/java/com/tendai/common/playback/QueueManager.kt) class to 
* retrieve track metadata
* build and maintain a queue 

and delegates playing the audio file to the [LocalPlayback](https://github.com/tendaimusakanye/TrackBud/blob/master/common/src/main/java/com/tendai/common/playback/LocalPlayback.kt) class.

## Screenshots
To be added soon.

## Contribute
Anyone can contribute

If you have any recommendations, suggestions, improvements or if you identify any bug 
1. File an issue describing the change
2. If your change accepted, fork the repo, develop and test your code changes
3. Ensure that your code style adheres to the [kotlin code style guide](https://developer.android.com/kotlin/style-guide)
4. Unit test your code if this is applicable
5. Submit a pull request. 

## Credits
[UAMP](https://github.com/android/uamp)

[TimberX](https://github.com/naman14/TimberX)

