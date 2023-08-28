import dk.sdu.mmmi.cbse.weaponsystem.WeaponControlSystem;
import dk.sdu.mmmi.cbse.weaponsystem.WeaponPlugin;
import dk.sdu.mmmi.cbse.common.weapon.WeaponSPI;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

module Weapon {
    requires Common;
    requires CommonWeapon;
    provides IGamePluginService with WeaponPlugin;
    provides WeaponSPI with WeaponControlSystem;
    provides IEntityProcessingService with WeaponControlSystem;
}