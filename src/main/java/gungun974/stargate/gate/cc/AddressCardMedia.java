package gungun974.stargate.gate.cc;

import dan200.computercraft.api.filesystem.FileOperationException;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.items.ItemAddressCard;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AddressCardMedia implements IMedia {
	@Override
	public @Nullable String getLabel(@NotNull ItemStack stack) {
		return stack.hasCustomName() ? stack.getCustomName() : null;
	}

	@Override
	public IMount createDataMount(@Nonnull ItemStack stack, @Nonnull World world) {
		return new AddressCardMount(stack);
	}

	private static class AddressCardMount implements IMount {
		private final ItemStack stack;

		public AddressCardMount(ItemStack stack) {
			this.stack = stack;
		}

		private byte[] getData() {
			StringBuilder content = new StringBuilder();
			content.append("{\n");

			appendFamily(content, StargateFamily.MilkyWay);
			appendFamily(content, StargateFamily.Pegasus);
			appendFamily(content, StargateFamily.Universe);

			content.append("}");
			return content.toString().getBytes(StandardCharsets.UTF_8);
		}

		private void appendFamily(StringBuilder content, StargateFamily family) {
			if (!ItemAddressCard.hasAddress(this.stack, family)) return;

			content.append("    ").append(family.name()).append(" = {\n");
			for (int i = 0; i < 9; i++) {
				content.append("        ")
					.append(ItemAddressCard.getSymbol(this.stack, family, i))
					.append(",\n");
			}
			content.append("    },\n");
		}

		@Override
		public boolean exists(@Nonnull String path) {
			return path.isEmpty() || path.equals("data");
		}

		@Override
		public boolean isDirectory(@Nonnull String path) {
			return path.isEmpty();
		}

		@Override
		public void list(@Nonnull String path, @Nonnull List<String> contents) {
			if (path.isEmpty()) {
				contents.add("data");
			}
		}

		@Override
		public long getSize(@Nonnull String path) {
			if (path.equals("data")) {
				return getData().length;
			}
			return 0;
		}

		@Nonnull
		@Override
		public ReadableByteChannel openForRead(@Nonnull String path) throws IOException {
			if (path.equals("data")) {
				return Channels.newChannel(new ByteArrayInputStream(getData()));
			}
			throw new FileOperationException(path, "No such file");
		}
	}
}
