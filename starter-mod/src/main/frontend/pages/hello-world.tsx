import { useState } from 'react';
import { HelloWorldService } from '../bridges/endpoints';
import { NotificationService } from '../components/notifications';
import Button from '../components/button';
import InputArea from '../components/input-area';

export default function HelloWorldView() {
  const [name, setName] = useState('');

  const handleValueChanged = (e: React.ChangeEvent<HTMLInputElement>) => {
    setName(e.target.value);
  };

  return (
    <>
      <InputArea
        label="Your name"
        value={name}
        // onValueChanged={handleValueChanged}
      />
      <Button
        onClick={async () => {
          await HelloWorldService.sayHello(name);
          NotificationService.show('Server response', 'error');
        }}>
        Say hello
      </Button>
    </>
  );
}
