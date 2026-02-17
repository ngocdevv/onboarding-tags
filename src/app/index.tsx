import { StyleSheet, View } from 'react-native';
import PhysicsWorld from '../components/PhysicsWorld';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <PhysicsWorld />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
});
