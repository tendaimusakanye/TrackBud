# TrackBud
TrackBud is a full-fledged music player for Android (Mobile ,TV, Wear) and is currently under development.
P.S. I am still working of finding a better name 🤣

The server side is almost complete though 😎
The client side ( UI for TV, Wear, Mobile ) is still at 0%
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
The screenshots below are still Adobe Xd designs.

![](https://db3pap007files.storage.live.com/y4mpGTWORFNu0GaK9rjdTomDbgHZPkzM1kv76YaHuU6pDAfYGD5SiAWTY0uN27sJi46OUXqkvFuPcMEUuiSJpuER0ChYsNKb1obE4AaBx6kt01xRqxWQKk9micBzEVqDtOIeiyiPiLPAVolqjUlosbwtX-_Oso7X2A_MnIiA5jMpjRTwo7T8rDvWIzo0ObaY9ff?width=310&height=547&cropmode=none)

![](https://db3pap007files.storage.live.com/y4mzv5x9QsBl6CfDCDZ6juiyLgIKMkmugRd_unnSWQsN66vyaFO6iCLOJQQWoL2dIPFq3sdNqUCVlwdz8aT5Ze-4si5Q5jkVsWajrwxBfQMuiwkcjazO99Xt9ogV7A98LVJMy392lue0UmF2BX7log3d5ubRNFHDVMPrcapOiGNQxVuyuDfWDImZ2R8rhYBweOO?width=313&height=546&cropmode=none)

![](https://db3pap007files.storage.live.com/y4mCchFc5MKZza6dq-06anM_rlJjt_FbLYYsdwxkKPQDnNBKiEgvxgw7z_XLswQpIdnBRpIWB1Nevyxo3DRZ2KWGCInBrPkqw1_7tZhy91tewuYuhnanb8GOtK-O84VZYGIbGxMFKCuMxGLq5J1NNX9L-yHkGAGspcVYH0UHr7ZsY-IARrn7MRGqkdZulVq4b8O?width=308&height=550&cropmode=none)
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

## Licence
I still don't know how these open source licences work. I will add one soon. 
