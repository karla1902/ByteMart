package Modelo;

public class TarjetasModelo {
    private int id;
    private int usuario_id;
    private String numero_tarjeta;
    private int mes_vencimiento;
    private int anio_vencimiento;
    private String codigo_verificacion;
    private int saldo;
    
    public TarjetasModelo(int id, int usuario_id, String numero_tajeta, int mes_vencimiento, int anio_vencimiento, String codigo_verificacion, int saldo){
        this.id = id;
        this.usuario_id = usuario_id;
        this.numero_tarjeta = numero_tajeta;
        this.mes_vencimiento = mes_vencimiento;
        this.anio_vencimiento = anio_vencimiento;
        this.codigo_verificacion = codigo_verificacion;
        this.saldo = saldo;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getUsuarioId() {
        return usuario_id;
    }

    public void setUsuarioId(int usuario_id) {
        this.usuario_id = usuario_id;
    }
    
    public String getNumeroTarjeta() {
        return numero_tarjeta;
    }

    public void setNumeroTarjeta(String numero_tarjeta) {
        this.numero_tarjeta = numero_tarjeta;
    }
    
    public int getMesVencimiento() {
        return mes_vencimiento;
    }

    public void setMesVencimiento(int mes_vencimiento) {
        this.mes_vencimiento = mes_vencimiento;
    }
    
    public int getAnioVencimiento() {
        return anio_vencimiento;
    }

    public void setAnioVencimiento(int anio_vencimiento) {
        this.anio_vencimiento = anio_vencimiento;
    }
    
    public String getCodigoVerificacion() {
        return codigo_verificacion;
    }

    public void setCodigoVerificacion(String codigo_verificacion) {
        this.codigo_verificacion = codigo_verificacion;
    }
    
    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
    
    // Devuelve datos de la tarjeta 
    @Override
    public String toString() {
        return "TarjetasModelo{" +
                "id=" + id +
                ", usuario_id=" + usuario_id +
                ", numero_tarjeta=" + numero_tarjeta +
                ", mes_vencimiento=" + mes_vencimiento +
                ", anio_vencimiento=" + anio_vencimiento +
                ", codigo_verificacion=" + codigo_verificacion +
                ", saldo=" + saldo +
                '}';
    }
}
