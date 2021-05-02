package seemdmax.lcpatches;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("seemdmax.lcpatches.")
@IFMLLoadingPlugin.Name("Lost Cities Patches")
public class LCPatchesPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		System.out.println("Got ASMTransformer");
		return new String[] { LostCitiesClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return LCPatchesModContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}