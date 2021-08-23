package my.gdx.game.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class removedEntity extends Entity{

    private static final long serialVersionUID = 1L;

    public removedEntity(long ID){
        super(ID); 
    }

    public void Serialize(DataOutputStream s) throws IOException{
        s.writeLong(-1);
        s.writeLong(ID); 
    }

    public void Deserialize(DataInputStream s) throws IOException{
    }
    
}
