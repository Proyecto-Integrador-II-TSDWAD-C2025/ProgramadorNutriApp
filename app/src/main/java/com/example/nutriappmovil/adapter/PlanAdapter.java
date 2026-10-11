package com.example.nutriappmovil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutriappmovil.PlanOpciones;
import com.example.nutriappmovil.R;
import com.example.nutriappmovil.model.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Lista de planes. Los botones Editar / Eliminar solo se muestran si
 * puedeGestionar es true (rol administrador o nutricionista). Es solo una
 * comodidad de la interfaz: Django vuelve a validar el permiso en cada pedido.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    public interface OnPlanAccionListener {
        void onEditar(Plan plan);

        void onEliminar(Plan plan);
    }

    private final Context context;
    private final OnPlanAccionListener listener;
    private final List<Plan> planes = new ArrayList<>();
    private boolean puedeGestionar = false;

    public PlanAdapter(Context context, OnPlanAccionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setPuedeGestionar(boolean puedeGestionar) {
        this.puedeGestionar = puedeGestionar;
        notifyDataSetChanged();
    }

    public void reemplazar(List<Plan> nuevos) {
        planes.clear();
        planes.addAll(nuevos);
        notifyDataSetChanged();
    }

    public void agregar(List<Plan> mas) {
        int inicio = planes.size();
        planes.addAll(mas);
        notifyItemRangeInserted(inicio, mas.size());
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planes.get(position);

        String objetivo = PlanOpciones.etiqueta(
                context, R.array.plan_objetivo_values, R.array.plan_objetivo_labels, plan.getObjetivo());
        String nivel = PlanOpciones.etiqueta(
                context, R.array.plan_nivel_actividad_values, R.array.plan_nivel_actividad_labels,
                plan.getNivelActividad());

        int dias = plan.getDuracionDias();

        holder.tvNombre.setText(plan.getNombrePlan());
        // El estado va con texto, no solo con color
        holder.tvEstado.setText(plan.isActivo() ? "Activo" : "Inactivo");
        holder.tvDetalle.setText(objetivo + " · Actividad " + nivel.toLowerCase(Locale.getDefault()));
        holder.tvNumeros.setText(
                PlanOpciones.formatearNumero(plan.getCaloriasObjetivo()) + " kcal · "
                        + dias + (dias == 1 ? " día" : " días"));
        holder.tvDescripcion.setText(plan.getDescripcion());

        holder.layoutAcciones.setVisibility(puedeGestionar ? View.VISIBLE : View.GONE);

        // Cada botón nombra el plan, para que TalkBack no lea "Editar, Editar, Editar"
        holder.btnEditar.setContentDescription("Editar plan " + plan.getNombrePlan());
        holder.btnEliminar.setContentDescription("Eliminar plan " + plan.getNombrePlan());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(plan));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(plan));
    }

    @Override
    public int getItemCount() {
        return planes.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNombre;
        final TextView tvEstado;
        final TextView tvDetalle;
        final TextView tvNumeros;
        final TextView tvDescripcion;
        final View layoutAcciones;
        final Button btnEditar;
        final Button btnEliminar;

        PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePlanItem);
            tvEstado = itemView.findViewById(R.id.tvEstadoPlanItem);
            tvDetalle = itemView.findViewById(R.id.tvDetallePlanItem);
            tvNumeros = itemView.findViewById(R.id.tvNumerosPlanItem);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPlanItem);
            layoutAcciones = itemView.findViewById(R.id.layoutAccionesPlanItem);
            btnEditar = itemView.findViewById(R.id.btnEditarPlanItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPlanItem);
        }
    }
}