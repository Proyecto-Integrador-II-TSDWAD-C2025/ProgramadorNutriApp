package com.example.nutriappmovil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.nutriappmovil.multimedia.FormatoTiempo;

/**
 * Activity multimedia "Guía en video". Hija de DashboardActivity.
 * Reproduce un video corto incluido en la app (res/raw/plato_equilibrado.mp4)
 * con controles propios: reproducir o pausar, y reiniciar.
 *
 * El contenido del video también está escrito en la pantalla. Así, si el
 * recurso no se puede reproducir, se muestra el aviso con Reintentar y la
 * información sigue disponible.
 *
 * El video no tiene sonido, por eso no hay control de volumen.
 */
public class MultimediaActivity extends AppCompatActivity {

    private static final String ESTADO_POSICION = "posicion_ms";
    private static final long INTERVALO_AVANCE_MS = 250;

    private VideoView videoView;
    private View poster;
    private View progressCargando;
    private View layoutError;
    private ProgressBar progresoVideo;
    private TextView tvTiempo;
    private Button btnReproducir;
    private Button btnReiniciar;

    // true desde que el video quedó listo hasta que falla o se libera
    private boolean preparado;
    private boolean terminado;
    private int posicionGuardadaMs;
    private boolean recargarAlVolver;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable actualizarAvance = new Runnable() {
        @Override
        public void run() {
            mostrarAvance();
            handler.postDelayed(this, INTERVALO_AVANCE_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        videoView = findViewById(R.id.videoMultimedia);
        poster = findViewById(R.id.ivPosterMultimedia);
        progressCargando = findViewById(R.id.progressCargandoVideo);
        layoutError = findViewById(R.id.layoutErrorVideo);
        progresoVideo = findViewById(R.id.progresoVideo);
        tvTiempo = findViewById(R.id.tvTiempoVideo);
        btnReproducir = findViewById(R.id.btnReproducirVideo);
        btnReiniciar = findViewById(R.id.btnReiniciarVideo);

        // Encabezados para la navegación de TalkBack
        ViewCompat.setAccessibilityHeading(findViewById(R.id.tvTituloMultimedia), true);
        ViewCompat.setAccessibilityHeading(findViewById(R.id.tvTituloContenidoMultimedia), true);

        if (savedInstanceState != null) {
            posicionGuardadaMs = savedInstanceState.getInt(ESTADO_POSICION, 0);
        }

        btnReproducir.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                pausar();
            } else {
                reproducir();
            }
        });
        btnReiniciar.setOnClickListener(v -> reiniciar());
        findViewById(R.id.btnReintentarVideo).setOnClickListener(v -> {
            posicionGuardadaMs = 0;
            prepararVideo();
        });
        findViewById(R.id.btnVolverMultimedia).setOnClickListener(v -> finish());

        videoView.setOnPreparedListener(mediaPlayer -> alPreparar());
        videoView.setOnCompletionListener(mediaPlayer -> alTerminar());
        // Devolver true evita el diálogo de error del sistema: lo muestra la pantalla
        videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            mostrarErrorVideo();
            return true;
        });

        prepararVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Al salir de la pantalla se pausa y se recuerda por dónde iba
        if (preparado) {
            posicionGuardadaMs = terminado ? 0 : videoView.getCurrentPosition();
            pausar();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Al quedar oculta la pantalla, VideoView libera el reproductor
        recargarAlVolver = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recargarAlVolver) {
            recargarAlVolver = false;
            prepararVideo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ESTADO_POSICION, posicionGuardadaMs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(actualizarAvance);
        videoView.stopPlayback();
    }

    /** Carga el video. Si el recurso no existe o está dañado, muestra el aviso. */
    private void prepararVideo() {
        preparado = false;
        terminado = false;

        layoutError.setVisibility(View.GONE);
        poster.setVisibility(View.VISIBLE);
        progressCargando.setVisibility(View.VISIBLE);
        habilitarControles(false);
        mostrarBotonReproducir();
        mostrarAvance();

        try {
            videoView.setVideoURI(Uri.parse(
                    "android.resource://" + getPackageName() + "/" + R.raw.plato_equilibrado
            ));
        } catch (RuntimeException exception) {
            mostrarErrorVideo();
        }
    }

    // Se ejecuta en la primera carga y cada vez que la pantalla regresa del
    // segundo plano; por eso acá se retoma la posición guardada.
    private void alPreparar() {
        preparado = true;
        terminado = false;

        progressCargando.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        poster.setVisibility(View.VISIBLE);
        habilitarControles(true);

        if (posicionGuardadaMs > 0) {
            videoView.seekTo(posicionGuardadaMs);
        }

        mostrarBotonReproducir();
        mostrarAvance();
    }

    private void alTerminar() {
        terminado = true;
        handler.removeCallbacks(actualizarAvance);
        mostrarBotonReproducir();
        mostrarAvance();
    }

    private void reproducir() {
        if (!preparado) {
            return;
        }

        if (terminado) {
            terminado = false;
            videoView.seekTo(0);
        }

        poster.setVisibility(View.GONE);
        videoView.start();

        btnReproducir.setText(R.string.multimedia_pausar);
        btnReproducir.setContentDescription(getString(R.string.multimedia_pausar_descripcion));

        handler.removeCallbacks(actualizarAvance);
        handler.post(actualizarAvance);
    }

    private void pausar() {
        handler.removeCallbacks(actualizarAvance);

        if (preparado && videoView.isPlaying()) {
            videoView.pause();
        }

        mostrarBotonReproducir();
        mostrarAvance();
    }

    private void reiniciar() {
        if (!preparado) {
            return;
        }

        terminado = false;
        posicionGuardadaMs = 0;
        videoView.seekTo(0);
        reproducir();
    }

    /** Falla del recurso: tapa el reproductor, explica qué pasó y ofrece reintentar. */
    private void mostrarErrorVideo() {
        preparado = false;
        terminado = false;
        handler.removeCallbacks(actualizarAvance);

        progressCargando.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        habilitarControles(false);
        mostrarBotonReproducir();
        mostrarAvance();
    }

    private void mostrarBotonReproducir() {
        btnReproducir.setText(R.string.multimedia_reproducir);
        btnReproducir.setContentDescription(getString(R.string.multimedia_reproducir_descripcion));
    }

    private void habilitarControles(boolean habilitados) {
        btnReproducir.setEnabled(habilitados);
        btnReiniciar.setEnabled(habilitados);
        btnReproducir.setAlpha(habilitados ? 1f : 0.5f);
        btnReiniciar.setAlpha(habilitados ? 1f : 0.5f);
    }

    private void mostrarAvance() {
        int duracionMs = preparado ? videoView.getDuration() : 0;
        int posicionMs = 0;
        if (preparado) {
            posicionMs = terminado ? duracionMs : videoView.getCurrentPosition();
        }

        String posicion = FormatoTiempo.minutosYSegundos(posicionMs);
        String duracion = FormatoTiempo.minutosYSegundos(duracionMs);

        progresoVideo.setProgress(FormatoTiempo.porcentaje(posicionMs, duracionMs));
        tvTiempo.setText(getString(R.string.multimedia_tiempo, posicion, duracion));
        tvTiempo.setContentDescription(
                getString(R.string.multimedia_tiempo_descripcion, posicion, duracion)
        );
    }
}
