package main.java.amongUs;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.amongUs.AnimacionJugador;

public class Fabrica implements EntityFactory {

    @Spawns("pared")
    public Entity nuevaPared(SpawnData data) {
        return FXGL.entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("objeto")
    public Entity nuevoObjeto(SpawnData data) {
        return FXGL.entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("jugador")
    public Entity nuevoJugador(SpawnData data) {
        String nombre = data.hasKey("nombre") ? data.get("nombre") : "Jugador";
        Text nombreVisual = new Text(nombre);
        nombreVisual.setFill(Color.WHITE);
        nombreVisual.setFont(Font.font("Arial", 6));
        nombreVisual.setTranslateY(-1);
        nombreVisual.setTranslateX( (32 / 2.0) - (nombreVisual.getLayoutBounds().getWidth() / 2.0) );

        HitBox piesHitBox = new HitBox("pies", BoundingShape.box(20, 15));

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.JUGADOR)
                .bbox(piesHitBox)
                .with(new CollidableComponent(true))
                .with(new AnimacionJugador())
                .view(nombreVisual)
                .scale(1.5,1.5)
                .build();
    }
}