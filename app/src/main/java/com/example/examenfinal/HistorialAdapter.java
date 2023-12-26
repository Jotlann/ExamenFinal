package com.example.examenfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<HistorialRutas> rutas;
    private SignRutas signRutas;

    public HistorialAdapter(List<HistorialRutas> historialRutasList) {
        this.rutas = new ArrayList<>();
    }

    public void setSignRutas(SignRutas signRutas) {
        this.signRutas = signRutas;
    }

    public void setRutas(List<HistorialRutas> rutas) {
        if (rutas != null) {
            for (HistorialRutas ruta : rutas) {
                if (ruta == null) {
                    throw new NullPointerException("El elemento en la lista no puede ser nulo");
                }
            }
            this.rutas = rutas;
            notifyDataSetChanged();
        } else {
            throw new NullPointerException("La lista de rutas no debe ser nula");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorialRutas ruta = rutas.get(position);
        holder.bind(ruta, signRutas);
    }

    private String obtenerTiempoFormateado(long millis) {
        long segundos = millis / 1000;
        long minutos = (segundos / 60) % 60;
        long horas = segundos / 3600;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos % 60);
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNombre;
        private TextView textViewFecha;
        private TextView textViewDuracion;
        private TextView textViewCoordenadasIniciales;
        private TextView textViewCoordenadasFinales;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombreRuta2);
            textViewFecha = itemView.findViewById(R.id.textViewFechaRuta2);
            textViewDuracion = itemView.findViewById(R.id.textViewDuracionRuta2);
            textViewCoordenadasIniciales = itemView.findViewById(R.id.textViewCoordenadasIniciales2);
            textViewCoordenadasFinales = itemView.findViewById(R.id.textViewCoordenadasFinales2);
        }

        public void bind(HistorialRutas ruta, SignRutas signRutas) {
            textViewNombre.setText("Nombre: " + ruta.getNombre());
            textViewFecha.setText("Fecha: " + ruta.getFecha());
            textViewDuracion.setText("Duración: " + signRutas.obtenerTiempoFormateadoPublic(ruta.getTiempoContadorSegundos()));
            textViewCoordenadasIniciales.setText("Coordenadas Iniciales: " + ruta.getCoordenadasIniciales());
            textViewCoordenadasFinales.setText("Coordenadas Finales: " + ruta.getCoordenadasFinales());
        }
    }
}