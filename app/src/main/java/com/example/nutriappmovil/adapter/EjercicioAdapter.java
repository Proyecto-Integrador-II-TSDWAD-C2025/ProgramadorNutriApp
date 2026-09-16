package com.example.nutriappmovil.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.R;
import com.example.nutriappmovil.model.Ejercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar ejercicios agrupados por dia.
 * La lista contiene objetos mixtos: String (header de dia) y Ejercicio.
 */
public class EjercicioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EJERCICIO = 1;

    private final List<Object> items = new ArrayList<>();

    public void setItems(List<Object> nuevosItems) {
        items.clear();
        if (nuevosItems != null) {
            items.addAll(nuevosItems);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_EJERCICIO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dia_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ejercicio, parent, false);
            return new EjercicioViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String diaTexto = (String) items.get(position);
            ((HeaderViewHolder) holder).bind(diaTexto);
        } else if (holder instanceof EjercicioViewHolder) {
            Ejercicio ejercicio = (Ejercicio) items.get(position);
            ((EjercicioViewHolder) holder).bind(ejercicio);
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
        }

        void bind(String diaTexto) {
            tvDiaHeader.setText(diaTexto);
        }
    }

    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreEjercicio;
        private final TextView tvDescripcionEjercicio;
        private final TextView tvSeriesReps;
        private final TextView tvDuracion;
        private final TextView tvTagCompletado;
        private final View ivCompletado;

        EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEjercicio = itemView.findViewById(R.id.tvNombreEjercicio);
            tvDescripcionEjercicio = itemView.findViewById(R.id.tvDescripcionEjercicio);
            tvSeriesReps = itemView.findViewById(R.id.tvSeriesReps);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
            tvTagCompletado = itemView.findViewById(R.id.tvTagCompletado);
            ivCompletado = itemView.findViewById(R.id.ivCompletado);
        }

        void bind(Ejercicio ejercicio) {
            tvNombreEjercicio.setText(ejercicio.getNombre());
            tvDescripcionEjercicio.setText(ejercicio.getDescripcion());

            // Mostrar series/repeticiones o duracion
            if (ejercicio.getSeries() != null && ejercicio.getRepeticiones() != null && !ejercicio.getRepeticiones().isEmpty()) {
                tvSeriesReps.setVisibility(View.VISIBLE);
                tvDuracion.setVisibility(View.GONE);
                tvSeriesReps.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones());
            } else if (ejercicio.getDuracionMinutos() != null) {
                tvSeriesReps.setVisibility(View.GONE);
                tvDuracion.setVisibility(View.VISIBLE);
                tvDuracion.setText(ejercicio.getDuracionMinutos() + " minutos");
            } else {
                tvSeriesReps.setVisibility(View.GONE);
                tvDuracion.setVisibility(View.GONE);
            }

            // Estado completado
            if (ejercicio.isCompletadoHoy()) {
                tvTagCompletado.setVisibility(View.VISIBLE);
                ivCompletado.setVisibility(View.VISIBLE);
                itemView.setBackgroundResource(R.drawable.bg_completed);
            } else {
                tvTagCompletado.setVisibility(View.GONE);
                ivCompletado.setVisibility(View.GONE);
                itemView.setBackgroundResource(R.drawable.bg_card_exercise);
            }
        }
    }
}
