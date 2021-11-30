import {ChangeEvent, FormEvent, useEffect, useState} from "react";

import Icon from "../component/Icon";

import {useAccount} from "../hook/globalContext";
import {updateCurrentAccount} from "../action/account";

type CharacterSettings = {
    name: undefined | string,
    world: undefined | string,
};

const worldList = [
    {
        id: 4,
        name: "Aether",
        worlds: [
            {id: 73, name: "Adamantoise"},
            {id: 79, name: "Cactuar"},
            {id: 54, name: "Faerie"},
            {id: 63, name: "Gilgamesh"},
            {id: 40, name: "Jenova"},
            {id: 65, name: "Midgardsormr"},
            {id: 99, name: "Sargatanas"},
            {id: 57, name: "Siren"},
        ]
    },
    {
        id: 8,
        name: "Crystal",
        worlds: [
            {id: 91, name: "Balmung"},
            {id: 34, name: "Brynhildr"},
            {id: 74, name: "Coeurl"},
            {id: 62, name: "Diabolos"},
            {id: 81, name: "Goblin"},
            {id: 75, name: "Malboro"},
            {id: 37, name: "Mateus"},
            {id: 41, name: "Zalera"},
        ],
    },
    {
        id: 5,
        name: "Primal",
        worlds: [
            {id: 78, name: "Behemoth"},
            {id: 93, name: "Excalibur"},
            {id: 53, name: "Exodus"},
            {id: 35, name: "Famfrit"},
            {id: 95, name: "Hyperion"},
            {id: 55, name: "Lamia"},
            {id: 64, name: "Leviathan"},
            {id: 77, name: "Ultros"},
        ],
    },
];

const Settings = () => {
    const [account, setAccount] = useAccount();
    const [showSaved, setShowSaved] = useState(false);
    const [values, setValues] = useState({} as CharacterSettings);

    const onChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const name = e.currentTarget.getAttribute("name") as string;
        setValues({...values, [name]: e.target.value});
    }

    const reset = () => {
        const name = account?.name || "";
        let world = "0:0";
        if (account?.datacenterId) {
            world = `${account.datacenterId}:${account.worldId}`;
        }
        setValues({name, world});
    }

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault();

        let [dcId, wId] = values.world?.split(":") || ["0", "0"];
        const account = await updateCurrentAccount({
            name: values?.name || "",
            datacenterId: +dcId,
            worldId: +wId,
        });
        setAccount(account);
        setShowSaved(true);
    };

    useEffect(reset, [account]);

    useEffect(() => {
        if (showSaved) {
            const timeout = setTimeout(() => setShowSaved(false), 5000);
            return () => clearTimeout(timeout);
        }
    }, [showSaved]);

    return <>
        <form
            className="box"
            onReset={e => {
                e.preventDefault();
                reset();
            }}
            onSubmit={onSubmit}
        >
            {showSaved && <article className="message is-primary">
                <div className="message-header">
                    <p><Icon name="save"/> Settings Saved</p>
                    <button
                        className="delete"
                        aria-label="delete"
                        onClick={() => setShowSaved(false)}
                    />
                </div>
                <div className="message-body">Successfully saved character settings</div>
            </article>}
            <div className="field">
                <label className="label">Character Name</label>
                <div className="control">
                    <input
                        className="input"
                        type="text"
                        name="name"
                        onChange={onChange}
                        value={values.name || ""}
                    />
                </div>
            </div>
            <div className="field">
                <label className="label">World</label>
                <div className="control has-icons-left">
                    <div className="select">
                        <select name="world" onChange={onChange} value={values.world || "0:0"}>
                            <option value="0:0">--- Select One ---</option>
                            {worldList.map(dc =>
                                <optgroup key={dc.id} label={dc.name}>
                                    {dc.worlds.map(w => <option
                                        children={w.name}
                                        key={w.id}
                                        value={`${dc.id}:${w.id}`}
                                    />)}
                                </optgroup>
                            )}
                        </select>
                    </div>
                    <Icon className="is-left" name="globe" />
                </div>
            </div>
            <div className="field is-grouped is-grouped-right">
                <div className="control">
                    <button className="button is-primary" type="submit">
                        <Icon name="save"/>
                        <span>Submit</span>
                    </button>
                </div>
                <div className="control">
                    <button className="button" type="reset">
                        <Icon name="undo"/>
                        <span>Reset</span>
                    </button>
                </div>
            </div>
        </form>
    </>;
};

export default Settings;
