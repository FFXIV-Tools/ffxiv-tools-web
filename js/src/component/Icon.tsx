export type IconProps = {
    name: string,
};

const Icon = ({name}: IconProps) =>
    <span className="icon">
        <i className={`fas fa-${name}`}/>
    </span>;

export default Icon;
