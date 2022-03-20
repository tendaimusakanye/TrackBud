package com.tendai.musicx.di

import android.content.Context
import com.tendai.musicx.ui.album.di.AlbumDetailsComponent
import com.tendai.musicx.ui.artist.di.ArtistAlbumsComponent
import com.tendai.musicx.ui.artist.di.ArtistComponent
import com.tendai.musicx.ui.artist.di.ArtistTracksComponent
import com.tendai.musicx.ui.discover.di.DiscoverComponent
import com.tendai.musicx.ui.main.di.MainComponent
import com.tendai.musicx.ui.playlist.di.AddToPlaylistComponent
import com.tendai.musicx.ui.playlist.di.PlaylistDetailsComponent
import com.tendai.musicx.ui.tracks.di.TracksComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope


@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class AppScope

@Component(modules = [ViewModelFactoryModule::class, AppModule::class, SubComponentsModule::class])
@AppScope
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun artistComponent(): ArtistComponent.Factory
    fun artistTracksComponent(): ArtistTracksComponent.Factory
    fun artistAlbumsComponent(): ArtistAlbumsComponent.Factory
    fun albumDetailsComponent(): AlbumDetailsComponent.Factory
    fun discoverComponent(): DiscoverComponent.Factory
    fun mainComponent(): MainComponent.Factory
    fun addToPlaylistComponent(): AddToPlaylistComponent.Factory
    fun playlistDetailsComponent(): PlaylistDetailsComponent.Factory
    fun tracksComponent(): TracksComponent.Factory
}

@Module(
    subcomponents = [
        AlbumDetailsComponent::class,
        ArtistComponent::class,
        ArtistTracksComponent::class,
        ArtistAlbumsComponent::class,
        DiscoverComponent::class,
        MainComponent::class,
        AddToPlaylistComponent::class,
        PlaylistDetailsComponent::class,
        TracksComponent::class]
)
object SubComponentsModule
