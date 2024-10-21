"""empty message

Revision ID: 09cb866da069
Revises: 39baff45d279
Create Date: 2024-10-20 00:51:12.448650

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '09cb866da069'
down_revision = '39baff45d279'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('tarjetas', schema=None) as batch_op:
        batch_op.create_unique_constraint('unique_tarjeta_usuario', ['numero_tarjeta', 'usuario_id'])

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('tarjetas', schema=None) as batch_op:
        batch_op.drop_constraint('unique_tarjeta_usuario', type_='unique')

    # ### end Alembic commands ###