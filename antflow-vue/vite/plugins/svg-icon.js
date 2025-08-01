import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";

export default function createSvgIcon(isBuild) {
  return createSvgIconsPlugin({
    iconDirs: [
      path.resolve(process.cwd(), "src/assets/icons/svg"),
      path.resolve(process.cwd(), "src/assets/icons/nodeSvg"),
    ],
    symbolId: "icon-[dir]-[name]",
    svgoOptions: isBuild,
  });
}
