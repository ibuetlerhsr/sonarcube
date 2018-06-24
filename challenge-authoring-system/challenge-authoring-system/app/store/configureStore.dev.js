import { applyMiddleware, createStore} from 'redux';
import challengesApp from '../reducers/reducers';
import thunkMiddleware from 'redux-thunk';

const createStoreWithMiddleware = applyMiddleware(thunkMiddleware)(createStore);

const store = createStoreWithMiddleware(challengesApp);

export function configureStore() {
    return store;
}
