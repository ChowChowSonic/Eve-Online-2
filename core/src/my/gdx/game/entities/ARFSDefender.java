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
        range = 35000 * (this.size + target.size) * METER;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void update(float deltaTime) {
        timer += deltaTime;
        Vector3 accelnorm = this.pos.cpy().sub(target.pos).nor();
        if (timer >= 5.0f && this.pos.dst(target.pos) <= range) {
            this.vel.x /= 2;
            this.vel.y /= 2;
            this.vel.z /= 2;
            this.accel.setZero();
            target.dealDamage(20);
            timer = 0;
        } else if (this.pos.dst(target.pos) >= range * 10 && this.vel.len() > 10) {
            this.addVel(
                    (float) (accelnorm.x * (deltaTime / Math.sqrt(this.mass + 1))
                            * ((1000 - (METER * this.mass)) - this.vel.len2())),
                    (float) (accelnorm.y * (deltaTime / Math.sqrt(this.mass + 1))
                            * ((1000 - (METER * this.mass)) - this.vel.len2())),
                    (float) (accelnorm.z * (deltaTime / Math.sqrt(this.mass + 1))
                            * ((1000 - (METER * this.mass)) - this.vel.len2())));
        } else if (this.vel.len() > 10) {
            this.accel.set(accelnorm);
        }
        super.update(deltaTime);
    }

    public void setTarget(KillableEntity e) {
        this.target = e;
    }

    @Override
    public boolean touches(Entity e) {
        if (iswarping)
            return false;
        return super.touches(e);
    }
}
