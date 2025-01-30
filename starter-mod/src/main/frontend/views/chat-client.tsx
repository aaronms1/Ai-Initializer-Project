import React from 'react';
import MainMessageListArea from './components/main-message-list';
import MainMessageInput from './components/main-message-input';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  menu: { order: 1, icon: 'line-awesome/svg/comment-alt-solid.svg' }, title: 'Chat',
};

/**
 * <h1>{@link ChatClientView}</h1>
 * @constructor Generates the main chat client view.
 */
const ChatClientView: React.FC = () => {
  return (
    <div>
      <MainMessageListArea />
      <footer>
        <MainMessageInput onMessageSent={function(userRequest: string): void {
                  throw new Error('Function not implemented.');
              } } onError={function(error: string): void {
                  throw new Error('Function not implemented.');
              } } onLoading={function(loading: boolean): void {
                  throw new Error('Function not implemented.');
              } } />
      </footer>
    </div>
  );
};

export default ChatClientView;
