export type IconProps = {
    className?: string,
    name: string,
};

const Icon = ({className, name}: IconProps) =>
    <span className={`icon ${className || ""}`}>
        <i className={`fas fa-${name}`}/>
    </span>;

export default Icon;
