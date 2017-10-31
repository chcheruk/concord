import React, {Component} from "react";
import PropTypes from "prop-types";
import {connect} from "react-redux";
import {Button, Header, Popup} from "semantic-ui-react";
import DataTable from "../../shared/DataTable";
import RefreshButton from "../../shared/RefreshButton";
import ErrorMessage from "../../shared/ErrorMessage";
import {actions as modal} from "../../shared/Modal";
import DeleteSecretPopup from "./DeleteSecretPopup";
import ShowSecretPublicKey from "./ShowSecretPublicKey";
import * as actions from "./actions";
import * as selectors from "./reducers";
import reducers from "./reducers";
import sagas from "./sagas";

const columns = [
    {key: "name", label: "Name", collapsing: true},
    {key: "type", label: "Type"},
    {key: "actions", label: "Actions", collapsing: true}
];

const nameKey = "name";
const actionsKey = "actions";

const cellFn = (deletePopupFn, getPublicKey) => (row, key) => {

    if (key === nameKey) {
        return <span>{row[key]}</span>;
    }

    console.log( row, key );

    // column with buttons (actions)
    if (key === actionsKey) {
        const name = row[nameKey];
        return (
            <div style={{ textAlign: "right"}}>
                { row.type === "KEY_PAIR" &&
                    <Popup
                        trigger={<Button color="blue" icon='key' onClick={() => getPublicKey(name)} />}
                        content="Get Public Key"
                        inverted
                    />
                }

                    <Popup
                        trigger={<Button icon="delete" color="red" onClick={() => deletePopupFn(name)} />}
                        content="Delete"
                        inverted
                    />
            </div>
        );
    }

    return row[key];
};

class SecretTable extends Component {

    componentDidMount() {
        this.update();
    }

    update() {
        const {fetchData} = this.props;
        fetchData();
    }

    render() {
        const {error, loading, data, deletePopupFn, getPublicKey} = this.props;

        if (error) {
            return <ErrorMessage message={error} retryFn={() => this.update()}/>;
        }

        return <div>
            <Header as="h3"><RefreshButton loading={loading} onClick={() => this.update()}/>Secrets</Header>
            <DataTable cols={columns} rows={data} cellFn={cellFn(deletePopupFn, getPublicKey)} />
        </div>;
    }
}

SecretTable.propTypes = {
    error: PropTypes.string,
    loading: PropTypes.bool,
    data: PropTypes.array,
    deletePopupFn: PropTypes.func,
};

const mapStateToProps = ({secretList}) => ({
    error: selectors.getError(secretList),
    loading: selectors.getIsLoading(secretList),
    data: selectors.getRows(secretList)
});

const mapDispatchToProps = (dispatch) => ({
    fetchData: () => dispatch(actions.fetchSecretList()),
    deletePopupFn: ( name ) => {
        const onSuccess = [actions.fetchSecretList()];
        dispatch(modal.open(DeleteSecretPopup.MODAL_TYPE, {name, onSuccess}));
    },
    getPublicKey: ( name ) => {
        dispatch( modal.open(ShowSecretPublicKey.MODAL_TYPE, { name }) );
        dispatch({
            type: actions.types.USER_SECRET_PUBLICKEY_REQUEST,
            name
        });
    }
});

export default connect(mapStateToProps, mapDispatchToProps)(SecretTable);

export {reducers, sagas};