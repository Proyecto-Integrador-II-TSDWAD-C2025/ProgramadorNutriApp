
package com.example.nutriappmovil;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.nutriappmovil.model.EditarUsuarioRequest;
import com.example.nutriappmovil.model.Usuario;
import com.example.nutriappmovil.model.UsuariosResponse;
import com.example.nutriappmovil.network.ApiClient;
import com.example.nutriappmovil.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosActivity extends AppCompatActivity {

    private ListView listaUsuarios;
    private ProgressBar progressUsuarios;
    private TextView tvMensajeUsuarios;
    private ArrayAdapter<String> adapter;

    private final List<String> nombresUsuarios = new ArrayList<>();
    private final List<Usuario> usuariosCargados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        listaUsuarios = findViewById(R.id.listaUsuarios);
        progressUsuarios = findViewById(R.id.progressUsuarios);
        tvMensajeUsuarios = findViewById(R.id.tvMensajeUsuarios);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nombresUsuarios
        );

        listaUsuarios.setAdapter(adapter);

        // Mantener presionado un usuario para mostrar las opciones.
        listaUsuarios.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    if (position < 0
                            || position >= usuariosCargados.size()) {
                        return true;
                    }

                    Usuario usuario = usuariosCargados.get(position);

                    String[] opciones = {"Editar", "Eliminar"};

                    new AlertDialog.Builder(UsuariosActivity.this)
                            .setTitle(
                                    usuario.getNombre() + " "
                                            + usuario.getApellido()
                            )
                            .setItems(opciones, (dialog, which) -> {

                                if (which == 0) {
                                    editarUsuario(usuario);
                                } else if (which == 1) {
                                    confirmarEliminacion(usuario);
                                }

                            })
                            .setNegativeButton("Cancelar", null)
                            .show();

                    return true;
                }
        );

        cargarUsuarios();
    }

    // Cargar los usuarios desde la API.
    private void cargarUsuarios() {

        progressUsuarios.setVisibility(View.VISIBLE);
        tvMensajeUsuarios.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient(this)
                .create(ApiService.class);

        apiService.listarUsuarios().enqueue(
                new Callback<UsuariosResponse>() {

                    @Override
                    public void onResponse(
                            Call<UsuariosResponse> call,
                            Response<UsuariosResponse> response
                    ) {

                        progressUsuarios.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            nombresUsuarios.clear();
                            usuariosCargados.clear();

                            List<Usuario> usuarios =
                                    response.body().getResults();

                            if (usuarios != null) {

                                for (Usuario usuario : usuarios) {

                                    usuariosCargados.add(usuario);

                                    String nombre =
                                            usuario.getNombre() != null
                                                    ? usuario.getNombre() : "";

                                    String apellido =
                                            usuario.getApellido() != null
                                                    ? usuario.getApellido() : "";

                                    String email =
                                            usuario.getEmail() != null
                                                    ? usuario.getEmail() : "";

                                    nombresUsuarios.add(
                                            nombre + " " + apellido
                                                    + "\n" + email
                                    );
                                }
                            }

                            adapter.notifyDataSetChanged();

                            if (nombresUsuarios.isEmpty()) {
                                tvMensajeUsuarios.setText(
                                        "No hay usuarios para mostrar."
                                );
                                tvMensajeUsuarios.setVisibility(View.VISIBLE);
                            }

                        } else {

                            tvMensajeUsuarios.setText(
                                    "No se pudieron cargar los usuarios. Código: "
                                            + response.code()
                            );

                            tvMensajeUsuarios.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UsuariosResponse> call,
                            Throwable t
                    ) {

                        progressUsuarios.setVisibility(View.GONE);

                        tvMensajeUsuarios.setText(
                                "Error de conexión: " + t.getMessage()
                        );

                        tvMensajeUsuarios.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    // Mostrar el formulario de edición.

    private void editarUsuario(Usuario usuario) {

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre");
        inputNombre.setSingleLine(true);
        inputNombre.setText(usuario.getNombre());

        EditText inputApellido = new EditText(this);
        inputApellido.setHint("Apellido");
        inputApellido.setSingleLine(true);
        inputApellido.setText(usuario.getApellido());

        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Correo electrónico");
        inputEmail.setSingleLine(true);
        inputEmail.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        inputEmail.setText(usuario.getEmail());

        LinearLayout formulario = new LinearLayout(this);
        formulario.setOrientation(LinearLayout.VERTICAL);

        int margen = (int) (20 * getResources()
                .getDisplayMetrics().density);

        formulario.setPadding(margen, 0, margen, 0);

        formulario.addView(inputNombre);
        formulario.addView(inputApellido);
        formulario.addView(inputEmail);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Editar usuario")
                .setView(formulario)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", null)
                .create();

        dialogo.setOnShowListener(d -> {

            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String nombre = inputNombre.getText()
                                .toString().trim();

                        String apellido = inputApellido.getText()
                                .toString().trim();

                        String email = inputEmail.getText()
                                .toString().trim();

                        if (nombre.isEmpty()
                                || apellido.isEmpty()
                                || email.isEmpty()) {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Completá todos los campos",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (!android.util.Patterns.EMAIL_ADDRESS
                                .matcher(email).matches()) {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Ingresá un correo electrónico válido",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        EditarUsuarioRequest request =
                                new EditarUsuarioRequest(
                                        nombre,
                                        apellido,
                                        email,
                                        usuario.getRol().getIdRol()
                                );

                        ApiService apiService =
                                ApiClient.getClient(UsuariosActivity.this)
                                        .create(ApiService.class);

                        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);

                        apiService.editarUsuario(
                                usuario.getIdUsuario(),
                                request
                        ).enqueue(new Callback<Void>() {

                            @Override
                            public void onResponse(
                                    Call<Void> call,
                                    Response<Void> response
                            ) {

                                if (response.isSuccessful()) {

                                    Toast.makeText(
                                            UsuariosActivity.this,
                                            "Usuario actualizado correctamente",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialogo.dismiss();
                                    cargarUsuarios();

                                } else {
                                    String detalle = "El servidor no informó el motivo.";

                                    try {
                                        if (response.errorBody() != null) {
                                            detalle = response.errorBody().string();
                                        }
                                    } catch (Exception e) {
                                        detalle = e.getMessage();
                                    }

                                    android.util.Log.e(
                                            "EDITAR_USUARIO",
                                            "Código: " + response.code()
                                                    + " - Detalle: " + detalle
                                    );

                                    dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                                            .setEnabled(true);

                                    Toast.makeText(
                                            UsuariosActivity.this,
                                            "Error " + response.code() + ": " + detalle,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                                }


                            @Override
                            public void onFailure(
                                    Call<Void> call,
                                    Throwable t
                            ) {

                                dialogo.getButton(
                                        AlertDialog.BUTTON_POSITIVE
                                ).setEnabled(true);

                                Toast.makeText(
                                        UsuariosActivity.this,
                                        "Error de conexión: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    });
        });

        dialogo.show();
    }


    // Pedir confirmación antes de eliminar.
    private void confirmarEliminacion(Usuario usuario) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage(
                        "¿Querés eliminar a "
                                + usuario.getNombre() + " "
                                + usuario.getApellido() + "?"
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) ->
                        eliminarUsuario(usuario))
                .show();
    }

    // Enviar la solicitud de eliminación a la API.
    private void eliminarUsuario(Usuario usuario) {

        ApiService apiService = ApiClient.getClient(this)
                .create(ApiService.class);

        apiService.eliminarUsuario(usuario.getIdUsuario())
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Usuario eliminado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            cargarUsuarios();

                        } else {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "No se pudo eliminar. Código: "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                UsuariosActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
