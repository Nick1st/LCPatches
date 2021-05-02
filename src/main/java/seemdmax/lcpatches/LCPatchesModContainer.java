package seemdmax.lcpatches;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class LCPatchesModContainer extends DummyModContainer {
	public LCPatchesModContainer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "lcpatches";
		meta.name = "Lost Cities Patches";
		meta.description = "Fixes mincellars flag";
		meta.version = "1.12.2";
		meta.authorList = Arrays.asList("SeemdmAx", "Nick1st");
		meta.credits = "josephcsible for helping us to fix some ASM issues; Windmill-City for the fix we are using";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		System.out.println("RegisterBus called");
		bus.register(this);
		return true;
	}
}