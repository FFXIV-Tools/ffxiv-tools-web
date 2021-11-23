export const iconImageSrc = (iconId: number): string => {
    const icon: string = iconId.toString().padStart(6, "0");
    return `https://xivapi.com/i/${icon.substr(0, 3)}000/${icon}.png`;
};
