
package Modelo;

public class RolUsuarioModelo {
    private final int id;
    private final int usuarioId;
    private final int rol_id;


    public RolUsuarioModelo(int id, int usuarioId, int rol_id) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.rol_id = rol_id;

    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }
    
    public int getRolId() {
        return rol_id;
    }

}
