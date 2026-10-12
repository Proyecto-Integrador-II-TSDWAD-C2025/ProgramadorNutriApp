package com.example.nutriappmovil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.R;
import com.example.nutriappmovil.model.Comida;
import com.example.nutriappmovil.model.PlanComida;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter del menú del plan alimenticio, agrupado por día.
 * La lista contiene objetos mixtos: Integer (encabezado con el número de día)
 * y PlanComida. La arma MenuPlanAlimenticio.agruparPorDia().
 */
public class ComidaPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Avisa a la Activity que el usuario quiere marcar o desmarcar una comida. */
    public interface OnCompletarComidaListener {
        void onCompletarComida(PlanComida planComida);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMIDA = 1;

    // Con un payload el RecyclerView reutiliza la misma tarjeta al actualizarla:
    // no hay animación de reemplazo y TalkBack no pierde el foco del botón.
    private static final Object CAMBIO_DE_ESTADO = new Object();

    private final List<Object> items = new ArrayList<>();
    private final SolicitudesEnCurso enCurso;
    private final OnCompletarComidaListener listener;

    public ComidaPlanAdapter(SolicitudesEnCurso enCurso, OnCompletarComidaListener listener) {
        this.enCurso = enCurso;
        this.listener = listener;
    }

    public void setItems(List<Object> nuevosItems) {
        items.clear();
        if (nuevosItems != null) {
            items.addAll(nuevosItems);
        }
        notifyDataSetChanged();
    }

    /**
     * Cambia el estado de una comida y vuelve a dibujar su tarjeta. También
     * sirve para refrescar el botón cuando empieza o termina una solicitud.
     * Si la comida ya no está en la lista (el plan cambió), no hace nada.
     */
    public void actualizarEstado(int idPlanComida, boolean completadaHoy) {
        for (int posicion = 0; posicion < items.size(); posicion++) {
            Object item = items.get(posicion);
            if (item instanceof PlanComida
                    && ((PlanComida) item).getIdPlanComida() == idPlanComida) {
                ((PlanComida) item).setCompletadaHoy(completadaHoy);
                notifyItemChanged(posicion, CAMBIO_DE_ESTADO);
                return;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Integer) {
            return TYPE_HEADER;
        }
        return TYPE_COMIDA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    inflater.inflate(R.layout.item_dia_header, parent, false)
            );
        }

        return new ComidaViewHolder(
                inflater.inflate(R.layout.item_comida_plan, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((Integer) items.get(position));
        } else if (holder instanceof ComidaViewHolder) {
            ((ComidaViewHolder) holder).bind((PlanComida) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDiaHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiaHeader = itemView.findViewById(R.id.tvDiaHeader);
            // TalkBack permite saltar de día en día con la navegación por encabezados
            ViewCompat.setAccessibilityHeading(tvDiaHeader, true);
        }

        void bind(int dia) {
            tvDiaHeader.setText(
                    itemView.getContext().getString(R.string.plan_alim_dia, dia)
            );
        }
    }

    class ComidaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTipoComida;
        private final TextView tvNombreComida;
        private final TextView tvPorcionComida;
        private final TextView tvMacrosComida;
        private final TextView tvAlternativaComida;
        private final TextView tvCompletadaComida;
        private final Button btnCompletarComida;

        ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoComida = itemView.findViewById(R.id.tvTipoComida);
            tvNombreComida = itemView.findViewById(R.id.tvNombreComida);
            tvPorcionComida = itemView.findViewById(R.id.tvPorcionComida);
            tvMacrosComida = itemView.findViewById(R.id.tvMacrosComida);
            tvAlternativaComida = itemView.findViewById(R.id.tvAlternativaComida);
            tvCompletadaComida = itemView.findViewById(R.id.tvCompletadaComida);
            btnCompletarComida = itemView.findViewById(R.id.btnCompletarComida);
        }

        void bind(PlanComida planComida) {
            Context context = itemView.getContext();
            Comida comida = planComida.getComida();
            String tipo = etiquetaTipo(context, planComida.getTipoComida());

            tvTipoComida.setText(tipo);

            mostrarOEsconder(tvNombreComida, comida != null ? comida.getNombre() : null);

            mostrarOEsconder(
                    tvPorcionComida,
                    estaVacio(planComida.getPorcion())
                            ? null
                            : context.getString(R.string.plan_alim_porcion, planComida.getPorcion())
            );

            mostrarOEsconder(
                    tvMacrosComida,
                    comida == null
                            ? null
                            : context.getString(
                                    R.string.plan_alim_macros,
                                    MenuPlanAlimenticio.formatearCantidad(comida.getCalorias()),
                                    MenuPlanAlimenticio.formatearCantidad(comida.getProteinas()),
                                    MenuPlanAlimenticio.formatearCantidad(comida.getCarbohidratos()),
                                    MenuPlanAlimenticio.formatearCantidad(comida.getGrasas())
                            )
            );

            mostrarOEsconder(
                    tvAlternativaComida,
                    estaVacio(planComida.getAlternativa())
                            ? null
                            : context.getString(R.string.plan_alim_alternativa, planComida.getAlternativa())
            );

            boolean completada = planComida.isCompletadaHoy();
            tvCompletadaComida.setVisibility(completada ? View.VISIBLE : View.GONE);
            itemView.setBackgroundResource(
                    completada ? R.drawable.bg_completed : R.drawable.card_fondo
            );

            itemView.setContentDescription(descripcionAccesible(context, completada));

            bindBoton(context, planComida, nombreParaMensajes(planComida, tipo));
        }

        /** Texto, estilo y disponibilidad del botón según el estado de la comida. */
        private void bindBoton(Context context, PlanComida planComida, String nombre) {
            boolean guardando = enCurso.contiene(planComida.getIdPlanComida());
            boolean completada = planComida.isCompletadaHoy();

            if (guardando) {
                btnCompletarComida.setText(R.string.plan_alim_guardando);
                btnCompletarComida.setContentDescription(
                        context.getString(R.string.plan_alim_guardando_descripcion, nombre)
                );
            } else if (completada) {
                btnCompletarComida.setText(R.string.plan_alim_desmarcar);
                btnCompletarComida.setContentDescription(
                        context.getString(R.string.plan_alim_desmarcar_descripcion, nombre)
                );
            } else {
                btnCompletarComida.setText(R.string.plan_alim_marcar);
                btnCompletarComida.setContentDescription(
                        context.getString(R.string.plan_alim_marcar_descripcion, nombre)
                );
            }

            // Pendiente: botón lleno (acción principal). Completada: botón con borde.
            btnCompletarComida.setBackgroundResource(
                    completada ? R.drawable.bg_button_secondary : R.drawable.bg_button_primary
            );
            btnCompletarComida.setTextColor(ContextCompat.getColor(
                    context,
                    completada ? R.color.primary : R.color.white
            ));

            // Deshabilitado mientras se guarda: evita el doble toque
            btnCompletarComida.setEnabled(!guardando);
            btnCompletarComida.setAlpha(guardando ? 0.6f : 1f);

            btnCompletarComida.setOnClickListener(v -> listener.onCompletarComida(planComida));
        }

        /** Une los textos visibles de la tarjeta y agrega el estado al final. */
        private String descripcionAccesible(Context context, boolean completada) {
            StringBuilder descripcion = new StringBuilder();

            TextView[] textos = {
                    tvTipoComida, tvNombreComida, tvPorcionComida, tvMacrosComida, tvAlternativaComida
            };
            for (TextView texto : textos) {
                if (texto.getVisibility() == View.VISIBLE) {
                    descripcion.append(texto.getText()).append(". ");
                }
            }

            descripcion.append(context.getString(
                    completada
                            ? R.string.plan_alim_estado_completada
                            : R.string.plan_alim_estado_pendiente
            ));

            return descripcion.toString();
        }
    }

    /** Nombre de la comida para avisos y lectores de pantalla; si falta, su tipo. */
    public static String nombreParaMensajes(PlanComida planComida, String tipo) {
        Comida comida = planComida.getComida();
        if (comida != null && !estaVacio(comida.getNombre())) {
            return comida.getNombre();
        }
        return tipo;
    }

    /** "desayuno" -> "Desayuno". Un tipo desconocido se muestra como llegó. */
    public static String etiquetaTipo(Context context, String tipoComida) {
        if (tipoComida == null) {
            return "";
        }

        switch (tipoComida) {
            case "desayuno":
                return context.getString(R.string.plan_alim_tipo_desayuno);
            case "almuerzo":
                return context.getString(R.string.plan_alim_tipo_almuerzo);
            case "merienda":
                return context.getString(R.string.plan_alim_tipo_merienda);
            case "cena":
                return context.getString(R.string.plan_alim_tipo_cena);
            case "snack":
                return context.getString(R.string.plan_alim_tipo_snack);
            default:
                return tipoComida;
        }
    }

    private static void mostrarOEsconder(TextView vista, String texto) {
        if (estaVacio(texto)) {
            vista.setVisibility(View.GONE);
        } else {
            vista.setText(texto);
            vista.setVisibility(View.VISIBLE);
        }
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
