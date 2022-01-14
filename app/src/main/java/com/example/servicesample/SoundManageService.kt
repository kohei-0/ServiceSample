package com.example.servicesample

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SoundManageService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    //test
    companion object{
        //通知チャンネルIDの文字列定数
        private const val CHANNEL_ID = "soundmanagerservice_notification_channel"
    }

    //todo private外してみると？
    private var _player: MediaPlayer? = null

    override fun onCreate() {
        _player = MediaPlayer()
        //通知ch名をstring.xmlから取得
        val name = getString(R.string.msg_notification_channel_name)
        //通知chの重要度を標準に設定
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        //通知chを生成
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        //NotificationManager obj を取得
        val manager = getSystemService(NotificationManager::class.java)
        //通知chを設定
        manager.createNotificationChannel(channel)
    }

    //todo ?外すと
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //音声ファイルのURI文字列を作成
        val mediaFileUriStr = "android.resource://${packageName}/${R.raw.moutain_stream}"
        //音声ファイルのURI文字列を元にURIオブジェクトを生成
        val mediaFileUri = Uri.parse(mediaFileUriStr)
        //propertyのplayerがnullでなかったら
        _player?.let {
            //mediaplayerに音声ファイルを指定
            it.setDataSource(this@SoundManageService, mediaFileUri)
            //非同期でのmedia再生準備が完了した際のリスナを設定
            it.setOnPreparedListener(PlayerPreparedListener())
            //メディア再生が終了した際のリスナを設定
            it.setOnCompletionListener(PlayerCompletionListener())
            //非同期でのメディア再生を準備
            it.prepareAsync()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        _player?.let {
            if(it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        _player = null
    }

    //メディア再生準備が完了したときのリスナクラス
    //TODO ここに追加
    private inner class PlayerPreparedListener : MediaPlayer.OnPreparedListener{
        override fun onPrepared(mp: MediaPlayer) {
            mp.start()
            //Nofiticationを作成するBuilderクラス生成
            val builder = NotificationCompat.Builder(this@SoundManageService, CHANNEL_ID)
            //通知エリアに表示されるアイコンを設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            //通知ドロワーでの表示タイトルを設定
            builder.setContentTitle(getString(R.string.msg_notification_title_start))
            //通知ドロワーでの表示メッセージを設定
            builder.setContentText(getString(R.string.msg_notification_text_satrt))
            //起動先Activityクラスを指定したIntent obj　生成
            val intent = Intent(this@SoundManageService, MainActivity::class.java)
            //起動先Activityに引き継ぎデータを格納
            intent.putExtra("fromNotification", true)
            //PendingIntentオブジェクトをビルダーに設定
            val stopServiceIntent = PendingIntent.getActivity(this@SoundManageService, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            //PendingIntent obj をbuilderに設定
            builder.setContentIntent(stopServiceIntent)
            //タップされた通知メッセージを自動的に消去するように設定
            builder.setAutoCancel(true)
            //BuilderからNotification objを生成
            val notification = builder.build()
            //Notification objを元にサービスをフォアグラウンド化
            startForeground(200, notification)

        }
    }

    //メディア再生が終了したときのリスナクラス
    private inner class PlayerCompletionListener : MediaPlayer.OnCompletionListener{
        override fun onCompletion(mp: MediaPlayer) {
            //Notificationを作成するBuilserクラス作成
            val builder = NotificationCompat.Builder(this@SoundManageService, CHANNEL_ID)
            //通知エリアに表示されるアイコンを設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            //通知ドロワーでの表示タイトルを設定
            builder.setContentTitle(getString(R.string.msg_notification_title_finish))
            //通知ドロワーでの表示メッセージを設定
            builder.setContentTitle(getString(R.string.msg_notification_text_finish))
            //BuilderからNotificationオブジェクトを生成
            val notification = builder.build()
            //NotificationManagerCompatオブジェクトを取得
            val manager = NotificationManagerCompat.from(this@SoundManageService)
            //通知
            manager.notify(100, notification)
            //自分自身を終了
            stopSelf()
        }
    }
}