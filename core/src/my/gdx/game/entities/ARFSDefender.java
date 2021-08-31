package my.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.Shipclass;

public class ARFSDefender extends NPC {
    private transient KillableEntity target;
    private transient float timer = 0, range;
    private boolean iswarping = false;

    public ARFSDefender(Shipclass Shiptype, KillableEntity target, long ID) {
        super(Shiptype, EntityType.ENFORCER, ID);
        this.target = target;
        range = 20 + (this.size + target.size);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void update(float deltaTime) {
        timer += deltaTime;
        if (target != null) {
            boolean isinrange = this.pos.dst(target.pos.cpy()) <= range;
            Vector3 accelnorm = this.pos.cpy().sub(target.pos.cpy()).nor();
            if (this.vel.isZero() && !isinrange && !iswarping)
                this.accel.set(-accelnorm.x, -accelnorm.y, -accelnorm.z);
            if (isinrange) {
                this.vel.x /= 1.5;
                this.vel.y /= 1.5;
                this.vel.z /= 1.5;
            }
            if (timer >= 1.0f && isinrange) {
                this.accel.setZero();
                target.dealDamage(20);
                timer = 0;
            } else if (this.pos.dst(target.pos.cpy()) >= range * 2 && this.pos.dst(target.pos.cpy()) <= range * 20) {
                this.addVel(
                        (float) (-accelnorm.x * (deltaTime / Math.sqrt(this.mass + 1))
                                * ((1000 - (METER * this.mass)) - this.vel.len2())),
                        (float) (-accelnorm.y * (deltaTime / Math.sqrt(this.mass + 1))
                                * ((1000 - (METER * this.mass)) - this.vel.len2())),
                        (float) (-accelnorm.z * (deltaTime / Math.sqrt(this.mass + 1))
                                * ((1000 - (METER * this.mass)) - this.vel.len2())));
            } else if (this.pos.dst(target.pos.cpy()) >= range * 20) {
                Vector3 targetpos = target.pos;
                double angle = Math.random() * Math.PI * 2f;
                this.pos = new Vector3(targetpos.x - (float) ((range / 2) * Math.sin(angle)),
                        targetpos.y - (float) ((range / 2) * Math.sin(angle) * Math.cos(angle)),
                        targetpos.z - (float) ((range / 2) * Math.cos(angle)));
            }
        }
        super.update(deltaTime);
    }

    public void setTarget(KillableEntity e) {
        this.target = e;
    }

    public Entity getTarget(){
        return this.target; 
    }

    public boolean touches(Entity e) {
        if (iswarping)
            return false;
        return super.touches(e);
    }
}
